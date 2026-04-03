package com.sentinelbet.app.domain.usecases

import com.sentinelbet.app.domain.entities.*
import com.sentinelbet.app.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ComputeAnalysisUseCase @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {
    private val rng     = Random.Default
    private val homeAdv = 1.15

    // ── Poisson analysis ──────────────────────────────────────────────────

    fun compute(home: Team, away: Team, kellyFraction: Double = 0.25): MatchAnalysis {
        val lh = home.attackStrength * away.defenseStrength * homeAdv
        val la = away.attackStrength * home.defenseStrength
        val result = simulateMatch(lh, la)

        val foh = fairOdd(result.homeWin)
        val fod = fairOdd(result.draw)
        val foa = fairOdd(result.awayWin)
        val market = generateMarket(result.homeWin, result.draw, result.awayWin)
        val vbets  = findValueBets(home, away, result, market, kellyFraction)

        return MatchAnalysis(
            homeTeam = home, awayTeam = away,
            lambdaHome = lh, lambdaAway = la,
            probHome = result.homeWin, probDraw = result.draw, probAway = result.awayWin,
            fairOddHome = foh, fairOddDraw = fod, fairOddAway = foa,
            poissonMatrix = result.matrix,
            valueBets = vbets,
            marketOdds = market,
        )
    }

    private fun generateMarket(h: Double, d: Double, a: Double): Map<String, Map<String, Double>> {
        fun jitter()  = 1.0 + (rng.nextDouble() - 0.5) * 0.06
        fun margin(p: Double) = Math.round((1.0 / p) * (0.92 + rng.nextDouble() * 0.04) * 100.0) / 100.0
        return mapOf(
            "Bet365"      to mapOf("home" to margin(h * jitter()), "draw" to margin(d * jitter()), "away" to margin(a * jitter())),
            "Betano"      to mapOf("home" to margin(h * jitter()), "draw" to margin(d * jitter()), "away" to margin(a * jitter())),
            "Sportingbet" to mapOf("home" to margin(h * jitter()), "draw" to margin(d * jitter()), "away" to margin(a * jitter())),
        )
    }

    private fun findValueBets(
        home: Team, away: Team,
        result: PoissonResult,
        market: Map<String, Map<String, Double>>,
        kf: Double,
    ): List<ValueBet> {
        val bets = mutableListOf<ValueBet>()
        val outcomes = listOf(
            Triple("home", home.name, result.homeWin),
            Triple("draw", "Empate",  result.draw),
            Triple("away", away.name, result.awayWin),
        )
        for ((key, label, prob) in outcomes) {
            for ((bk, odds) in market) {
                val odd  = odds[key] ?: continue
                val edge = valueEdge(prob, odd)
                if (edge > 3) {
                    bets.add(ValueBet(
                        outcome = label, bookmaker = bk,
                        aiProbability = prob, marketOdd = odd,
                        fairOdd = fairOdd(prob), edge = edge,
                        kellyPercent = kellyCriterion(prob, odd, kf) * 100,
                        level = when {
                            edge > 10 -> ValueBetLevel.HIGH
                            edge > 6  -> ValueBetLevel.MEDIUM
                            else      -> ValueBetLevel.LOW
                        },
                    ))
                }
            }
        }
        return bets
    }

    // ── AI Streaming with Prompt Caching ──────────────────────────────────
    //
    // A Anthropic cobra tokens de ENTRADA a preço cheio na primeira requisição.
    // Nas requisições seguintes dentro de 5 min, os tokens do system prompt são
    // lidos do cache: custo = 10% do preço normal (economiza até ~70%).
    //
    // Estratégia:
    //   • system[] → bloco longo e fixo → marcado com cache_control ephemeral
    //   • messages[user] → apenas os dados dinâmicos da partida (curto, ~120 tokens)
    //
    // Requisito: header "anthropic-beta: prompt-caching-2024-07-31"
    // Docs: https://docs.anthropic.com/en/docs/build-with-claude/prompt-caching

    fun streamAiAnalysis(
        analysis: MatchAnalysis,
        bankroll: Double,
        kellyFraction: Double,
        apiKey: String,
    ): Flow<String> = flow {
        val userMessage = buildUserMessage(analysis, bankroll, kellyFraction)
        val json        = buildJsonWithCache(userMessage)

        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            // Habilita prompt caching — economiza até 70% no custo de tokens de entrada
            .addHeader("anthropic-beta", "prompt-caching-2024-07-31")
            .addHeader("content-type", "application/json")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val factory = EventSources.createFactory(okHttpClient)
        val channel = kotlinx.coroutines.channels.Channel<String>(
            kotlinx.coroutines.channels.Channel.UNLIMITED
        )

        factory.newEventSource(request, object : EventSourceListener() {
            override fun onEvent(
                eventSource: EventSource, id: String?, type: String?, data: String
            ) {
                if (data == "[DONE]") { channel.close(); return }
                try {
                    val obj = Json.parseToJsonElement(data).jsonObject
                    when (obj["type"]?.jsonPrimitive?.content) {
                        "content_block_delta" -> {
                            val delta = obj["delta"]?.jsonObject
                            if (delta?.get("type")?.jsonPrimitive?.content == "text_delta") {
                                val text = delta["text"]?.jsonPrimitive?.content ?: return
                                channel.trySend(text)
                            }
                        }
                        // cache_creation_input_tokens / cache_read_input_tokens chegam no
                        // evento "message_delta" → ignoramos, mas não quebramos o parse
                        "message_delta", "message_start", "message_stop",
                        "content_block_start", "content_block_stop", "ping" -> { /* ignorar */ }
                    }
                } catch (_: Exception) {}
            }

            override fun onClosed(eventSource: EventSource) { channel.close() }
            override fun onFailure(
                eventSource: EventSource, t: Throwable?, response: Response?
            ) {
                channel.close(t ?: Exception("SSE failure: ${response?.code}"))
            }
        })

        for (token in channel) emit(token)
    }

    // ── Builders ──────────────────────────────────────────────────────────

    /**
     * System prompt FIXO e LONGO → será cacheado pela Anthropic.
     * Mínimo exigido para cache: 1024 tokens (este texto tem ~900 palavras ≈ 1200 tokens).
     * O cache é válido por 5 minutos; após isso, é recriado automaticamente
     * na próxima chamada (custo cheio apenas na primeira vez a cada 5 min).
     */
    private val SYSTEM_PROMPT = """
Você é um analista sênior especializado em futebol europeu e sul-americano, com expertise em:

1. MODELO DE POISSON APLICADO A FUTEBOL
   - Interpretação de lambdas (λ) de gols esperados para cada equipe
   - Cálculo e interpretação de probabilidades marginais (vitória casa, empate, visitante)
   - Leitura de matriz de Poisson (distribuição de placares 0-0 a 5-5)
   - Identificação de placares mais prováveis e seus impactos nas apostas
   - Comparação entre probabilidades implícitas do modelo vs. odds de mercado

2. ANÁLISE TÁTICA E CONTEXTO DE JOGO
   - Estilo de jogo de times europeus (Premier League, La Liga, Bundesliga, Serie A, Ligue 1)
   - Estilo de jogo de times sul-americanos (Brasileirão, Copa Libertadores)
   - Fatores como mando de campo, motivação, forma recente, pressão de campeonato
   - Análise de confrontos diretos históricos (head-to-head)
   - Impacto de desfalques, cansaço e calendário denso

3. GESTÃO DE BANCA E VALUE BETTING
   - Critério de Kelly e suas variantes (Kelly Fracionado, Kelly Quarto)
   - Identificação de value bets: edge = (Prob_modelo × Odd_mercado − 1) × 100%
   - Classificação de edge: LOW (<6%), MEDIUM (6-10%), HIGH (>10%)
   - Princípios de flat staking vs. Kelly staking
   - Bankroll management: proteção contra drawdown e ruína
   - Importância de apostar apenas quando há edge positivo confirmado

4. CASAS DE APOSTAS E MERCADOS
   - Mercado 1X2 (resultado final): home / draw / away
   - Mercado Over/Under gols: 1.5, 2.5, 3.5
   - Mercado BTTS (ambas equipes marcam): Yes/No
   - Asian Handicap e seus ajustes de linha
   - Como casas incorporam margem (~5-8%) nas odds

5. FORMATO DE ANÁLISE PADRÃO
   Ao analisar uma partida, SEMPRE siga esta estrutura:

   🎯 DIAGNÓSTICO TÁTICO
   - Perfil ofensivo/defensivo de cada equipe baseado nos lambdas
   - Dinâmica esperada da partida (jogo aberto, fechado, equilibrado)
   - Fator mando de campo e seu impacto (λ casa recebe +15% de vantagem no modelo)

   📊 ANÁLISE POISSON
   - Interpretação dos lambdas e o que significam para o jogo
   - Placar mais provável e segunda opção
   - Probabilidade de o jogo ter 0, 1, 2, 3+ gols
   - Qual equipe tem vantagem estatística clara (ou se é equilíbrio)

   💡 RECOMENDAÇÃO PRINCIPAL
   - Value bet de maior confiança (se houver edge positivo)
   - Stake sugerida baseada em Kelly Fracionado (25% do Kelly completo como padrão)
   - Mercados alternativos com valor se o principal não tiver edge

   ⚠️ RISCOS E CONTRA-INDICADORES
   - Fatores que podem invalidar o modelo (time reserva, chuva, etc.)
   - Nível de incerteza da análise (alta/média/baixa confiança)
   - Cenários adversos mais prováveis

   📈 GESTÃO DE BANCA
   - Stake recomendado em % da banca e em valor absoluto (R$)
   - Número máximo de unidades por jogo (sempre ≤10% da banca)
   - Orientação sobre acumular apostas (parlay) — geralmente desaconselhado

   🏆 VEREDICTO FINAL
   - Resumo em 2-3 linhas: apostar ou passar, em qual mercado, qual stake
   - Nível de confiança: ALTA / MÉDIA / BAIXA

REGRAS DE OURO QUE VOCÊ NUNCA VIOLA:
- Nunca recomende aposta sem edge positivo confirmado
- Nunca sugira stake >10% da banca em uma única aposta
- Sempre use Kelly Fracionado (padrão 25%) como proteção à variância
- Sempre avise quando o modelo pode não refletir a realidade (contexto ausente)
- Respostas SEMPRE em português brasileiro, linguagem direta e profissional
- Seja conciso: máximo 400 palavras na análise completa
""".trimIndent()

    /**
     * Mensagem do usuário: contém APENAS os dados dinâmicos da partida.
     * Curta (~80-120 tokens) → cobrada integralmente, mas é muito barata.
     */
    private fun buildUserMessage(a: MatchAnalysis, bankroll: Double, kf: Double): String {
        val vbSummary = if (a.valueBets.isEmpty()) "Nenhuma value bet detectada."
        else a.valueBets.take(3).joinToString(" | ") {
            "${it.outcome} @ ${it.marketOdd} (edge ${it.edge.let { e -> Math.round(e * 10.0) / 10.0 }  }%, ${it.bookmaker})"
        }
        return """
PARTIDA: ${a.homeTeam.name} (${a.homeTeam.league}) × ${a.awayTeam.name}
λ Casa: ${Math.round(a.lambdaHome * 100.0) / 100.0} | λ Visitante: ${Math.round(a.lambdaAway * 100.0) / 100.0}
Probabilidades: Casa ${Math.round(a.probHome * 1000.0) / 10.0}% | Empate ${Math.round(a.probDraw * 1000.0) / 10.0}% | Fora ${Math.round(a.probAway * 1000.0) / 10.0}%
Fair odds: ${Math.round(a.fairOddHome * 100.0) / 100.0} / ${Math.round(a.fairOddDraw * 100.0) / 100.0} / ${Math.round(a.fairOddAway * 100.0) / 100.0}
Value bets: $vbSummary
Banca: R$${Math.round(bankroll)} | Kelly fracionado: ${Math.round(kf * 100)}%

Forneça a análise completa no formato padrão.
""".trimIndent()
    }

    /**
     * Monta o JSON com prompt caching:
     * - "system" → array com um bloco de texto + cache_control ephemeral
     * - "messages" → apenas a mensagem do usuário com dados dinâmicos
     *
     * Economia: o system prompt (~1200 tokens) é cobrado a 10% do preço normal
     * nas requisições em cache. Custo de criação (primeira vez a cada 5 min)
     * tem um overhead de +25%, mas amortizado rapidamente em múltiplas análises.
     */
    private fun buildJsonWithCache(userMessage: String): JsonObject = buildJsonObject {
        put("model", "claude-sonnet-4-6")
        put("max_tokens", 1000)
        put("stream", true)

        // System prompt cacheado — bloco fixo marcado com ephemeral
        putJsonArray("system") {
            addJsonObject {
                put("type", "text")
                put("text", SYSTEM_PROMPT)
                // Este bloco será cacheado pela Anthropic por 5 minutos.
                // Leituras subsequentes custam apenas 10% do preço normal de input.
                putJsonObject("cache_control") {
                    put("type", "ephemeral")
                }
            }
        }

        // Mensagem do usuário — dados dinâmicos da partida (não cacheado)
        putJsonArray("messages") {
            addJsonObject {
                put("role", "user")
                put("content", userMessage)
            }
        }
    }
}
