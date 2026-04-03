package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinelbet.app.domain.entities.*
import com.sentinelbet.app.ui.components.*
import com.sentinelbet.app.ui.theme.*
import com.sentinelbet.app.viewmodel.*

@Composable
fun ValueBetsScreen(
    analysisVm: AnalysisViewModel   = hiltViewModel(),
    dashboardVm: DashboardViewModel = hiltViewModel(),
    isPro: Boolean                  = true,
    onUpgrade: () -> Unit           = {},
) {
    val state    by analysisVm.uiState.collectAsStateWithLifecycle()
    val analysis = when (state) {
        is AnalysisUiState.Ready     -> (state as AnalysisUiState.Ready).analysis
        is AnalysisUiState.Streaming -> (state as AnalysisUiState.Streaming).analysis
        else                         -> null
    }

    // For FREE users: limit visible value bets to 3
    val freeLimit   = 3
    val valueBets   = analysis?.valueBets ?: emptyList()
    val visibleBets = if (isPro) valueBets else valueBets.take(freeLimit)
    val hiddenCount = (valueBets.size - freeLimit).coerceAtLeast(0)

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        item {
            Text(
                "VALUE BETS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }

        // ── Match context strip ───────────────────────────────────────────
        if (analysis != null) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(analysis.homeTeam.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp), modifier = Modifier.weight(1f))
                        Text("VS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp), modifier = Modifier.padding(horizontal = 8.dp))
                        Text(analysis.awayTeam.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                }
            }
        }

        if (analysis == null) {
            item {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Text(
                            "Configure uma partida no Dashboard\npara detectar value bets.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        } else {
            if (analysis.valueBets.isEmpty()) {
                item {
                    SbCard {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                                Text(
                                    "Nenhuma value bet detectada.\nO mercado parece eficiente.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                "${analysis.valueBets.size} oportunidade(s) encontrada(s)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp),
                            )
                        }
                    }
                }
                items(visibleBets) { vb -> ValueBetCard(vb) }

                // ── Free paywall banner ─────────────────────────────────────
                if (!isPro && hiddenCount > 0) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.07f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp),
                            )
                            Text(
                                text = "$hiddenCount value bet(s) oculta(s)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "Assine o Plano Pro para ver todas as oportunidades.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                            Button(
                                onClick = onUpgrade,
                                shape   = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Ver Plano Pro", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item { OddsComparisonCard(analysis) }
        }

        item {
            Text(
                "⚠️ Edge calculado como (Prob_IA × Odd_Mercado − 1) × 100%. Apostas envolvem risco.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp),
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ── Value Bet Card ────────────────────────────────────────────────────────

@Composable
private fun ValueBetCard(vb: ValueBet) {
    val levelColor = when (vb.level) {
        ValueBetLevel.HIGH   -> Success
        ValueBetLevel.MEDIUM -> Warning
        ValueBetLevel.LOW    -> Info
    }
    val levelLabel = when (vb.level) {
        ValueBetLevel.HIGH   -> "ALTA"
        ValueBetLevel.MEDIUM -> "MÉDIA"
        ValueBetLevel.LOW    -> "BAIXA"
    }
    SbCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(vb.outcome.uppercase(), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp))
                LevelBadge(levelLabel, levelColor)
            }
            Text("📍 ${vb.bookmaker}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip("EDGE",    "${"%.1f".format(vb.edge)}%",                    Success,  Modifier.weight(1f))
                StatChip("PROB IA", "${"%.1f".format(vb.aiProbability * 100)}%",     Accent2,  Modifier.weight(1f))
                StatChip("KELLY",   "${"%.1f".format(vb.kellyPercent)}%",            Accent5,  Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip("ODD MERCADO", "${"%.2f".format(vb.marketOdd)}", Warning,        Modifier.weight(1f))
                StatChip("ODD FAIR",    "${"%.2f".format(vb.fairOdd)}",   MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun LevelBadge(label: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp))
        Spacer(Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color))
    }
}

// ── Odds Comparison ───────────────────────────────────────────────────────

@Composable
private fun OddsComparisonCard(analysis: MatchAnalysis) {
    SbCard {
        SbCardHeader(title = "Comparação de Odds")
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Text("", Modifier.weight(1f))
                for (bk in analysis.marketOdds.keys) {
                    Text(bk, Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            listOf("home" to "Casa", "draw" to "Empate", "away" to "Fora").forEach { (key, label) ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp))
                    for (bk in analysis.marketOdds.keys) {
                        val odd = analysis.marketOdds[bk]?.get(key) ?: 0.0
                        Text("${"%.2f".format(odd)}", Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(color = Accent2, fontSize = 14.sp, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}
