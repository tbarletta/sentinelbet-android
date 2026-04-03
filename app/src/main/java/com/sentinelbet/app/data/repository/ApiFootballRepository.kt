package com.sentinelbet.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

data class LiveFixture(
    val fixtureId: Int,
    val league: String,
    val leagueLogo: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeGoals: Int?,
    val awayGoals: Int?,
    val status: String,       // "1H", "HT", "2H", "FT", "NS", etc.
    val elapsed: Int?,        // minutos jogados
    val date: String,
    val homeOdd: Double?,
    val drawOdd: Double?,
    val awayOdd: Double?,
)

data class FixtureOdds(
    val homeOdd: Double?,
    val drawOdd: Double?,
    val awayOdd: Double?,
)

@Singleton
class ApiFootballRepository @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val settingsRepository: SettingsRepository,
) {
    companion object {
        private const val BASE_URL = "https://v3.football.api-sports.io"

        // IDs das ligas suportadas na API-Football
        val LEAGUE_IDS = mapOf(
            "Brasileirão Série A" to 71,
            "Premier League"      to 39,
            "La Liga"             to 140,
            "Bundesliga"          to 78,
            "Serie A"             to 135,
            "Ligue 1"             to 61,
            "Champions League"    to 2,
            "Copa Libertadores"   to 13,
        )
    }

    private suspend fun apiKey(): String? = settingsRepository.getApiFootballKey()

    private fun get(url: String, apiKey: String): JsonObject? {
        val request = Request.Builder()
            .url(url)
            .addHeader("x-apisports-key", apiKey)
            .build()
        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) return null
        val body = response.body?.string() ?: return null
        return Json.parseToJsonElement(body).jsonObject
    }

    /** Jogos ao vivo de todas as ligas suportadas */
    suspend fun getLiveFixtures(): Result<List<LiveFixture>> = withContext(Dispatchers.IO) {
        runCatching {
            val key = apiKey() ?: error("Chave API-Football não configurada")
            val leagueIds = LEAGUE_IDS.values.joinToString("-")
            val json = get("$BASE_URL/fixtures?live=$leagueIds", key)
                ?: error("Falha ao buscar jogos ao vivo")

            parseFixtures(json, includeOdds = false, liveOnly = true)
        }
    }

    /** Jogos do dia (hoje) por liga */
    suspend fun getTodayFixtures(leagueId: Int, season: Int = 2025): Result<List<LiveFixture>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val key = apiKey() ?: error("Chave API-Football não configurada")
                val today = java.time.LocalDate.now().toString() // yyyy-MM-dd
                val json = get(
                    "$BASE_URL/fixtures?league=$leagueId&season=$season&date=$today",
                    key
                ) ?: error("Falha ao buscar jogos de hoje")

                parseFixtures(json, includeOdds = false, liveOnly = false)
            }
        }

    /** Odds de um fixture específico (mercado 1x2) */
    suspend fun getOdds(fixtureId: Int): Result<FixtureOdds> = withContext(Dispatchers.IO) {
        runCatching {
            val key = apiKey() ?: error("Chave API-Football não configurada")
            val json = get("$BASE_URL/odds?fixture=$fixtureId&bet=1", key)
                ?: error("Falha ao buscar odds")

            parseOdds(json)
        }
    }

    /** Jogos ao vivo + odds em uma única chamada por liga */
    suspend fun getLiveFixturesWithOdds(): Result<List<LiveFixture>> = withContext(Dispatchers.IO) {
        runCatching {
            val key = apiKey() ?: error("Chave API-Football não configurada")
            val leagueIds = LEAGUE_IDS.values.joinToString("-")
            val fixturesJson = get("$BASE_URL/fixtures?live=$leagueIds", key)
                ?: error("Falha ao buscar jogos ao vivo")

            val fixtures = parseFixtures(fixturesJson, includeOdds = false, liveOnly = true)

            // Busca odds para cada jogo (até 5 jogos para não estourar cota)
            fixtures.take(5).map { fixture ->
                val oddsResult = runCatching {
                    val oddsJson = get("$BASE_URL/odds/live?fixture=${fixture.fixtureId}&bet=1", key)
                    oddsJson?.let { parseOdds(it) }
                }.getOrNull()
                fixture.copy(
                    homeOdd = oddsResult?.homeOdd,
                    drawOdd = oddsResult?.drawOdd,
                    awayOdd = oddsResult?.awayOdd,
                )
            } + fixtures.drop(5)
        }
    }

    // ── Parsers ──────────────────────────────────────────────────────────────

    private fun parseFixtures(
        json: JsonObject,
        includeOdds: Boolean,
        liveOnly: Boolean,
    ): List<LiveFixture> {
        val response = json["response"]?.jsonArray ?: return emptyList()
        return response.mapNotNull { item ->
            runCatching {
                val obj      = item.jsonObject
                val fixture  = obj["fixture"]?.jsonObject ?: return@runCatching null
                val league   = obj["league"]?.jsonObject  ?: return@runCatching null
                val teams    = obj["teams"]?.jsonObject   ?: return@runCatching null
                val goals    = obj["goals"]?.jsonObject
                val score    = obj["score"]?.jsonObject
                val status   = fixture["status"]?.jsonObject
                val statusShort = status?.get("short")?.jsonPrimitive?.content ?: "NS"

                LiveFixture(
                    fixtureId  = fixture["id"]?.jsonPrimitive?.int ?: return@runCatching null,
                    league     = league["name"]?.jsonPrimitive?.content ?: "",
                    leagueLogo = league["logo"]?.jsonPrimitive?.content ?: "",
                    homeTeam   = teams["home"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: "",
                    awayTeam   = teams["away"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: "",
                    homeGoals  = goals?.get("home")?.jsonPrimitive?.intOrNull,
                    awayGoals  = goals?.get("away")?.jsonPrimitive?.intOrNull,
                    status     = statusShort,
                    elapsed    = status?.get("elapsed")?.jsonPrimitive?.intOrNull,
                    date       = fixture["date"]?.jsonPrimitive?.content ?: "",
                    homeOdd    = null,
                    drawOdd    = null,
                    awayOdd    = null,
                )
            }.getOrNull()
        }
    }

    private fun parseOdds(json: JsonObject): FixtureOdds {
        val response = json["response"]?.jsonArray?.firstOrNull()?.jsonObject
            ?: return FixtureOdds(null, null, null)
        val bookmakers = response["bookmakers"]?.jsonArray?.firstOrNull()?.jsonObject
            ?: return FixtureOdds(null, null, null)
        val bets = bookmakers["bets"]?.jsonArray?.firstOrNull()?.jsonObject
            ?: return FixtureOdds(null, null, null)
        val values = bets["values"]?.jsonArray ?: return FixtureOdds(null, null, null)

        var home: Double? = null
        var draw: Double? = null
        var away: Double? = null

        values.forEach { v ->
            val obj   = v.jsonObject
            val value = obj["value"]?.jsonPrimitive?.content
            val odd   = obj["odd"]?.jsonPrimitive?.content?.toDoubleOrNull()
            when (value) {
                "Home" -> home = odd
                "Draw" -> draw = odd
                "Away" -> away = odd
            }
        }
        return FixtureOdds(home, draw, away)
    }
}
