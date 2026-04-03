package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import com.sentinelbet.app.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinelbet.app.data.db.BetEntity
import com.sentinelbet.app.data.db.BetResult
import com.sentinelbet.app.domain.entities.*
import com.sentinelbet.app.ui.components.*
import com.sentinelbet.app.ui.theme.*
import com.sentinelbet.app.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardVm: DashboardViewModel = hiltViewModel(),
    analysisVm: AnalysisViewModel   = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {},
) {
    val dashState     by dashboardVm.uiState.collectAsStateWithLifecycle()
    val analysisState by analysisVm.uiState.collectAsStateWithLifecycle()

    val selectedLeague by analysisVm.selectedLeague.collectAsStateWithLifecycle()
    val homeTeam       by analysisVm.homeTeam.collectAsStateWithLifecycle()
    val awayTeam       by analysisVm.awayTeam.collectAsStateWithLifecycle()
    val teams = getTeamsForLeague(selectedLeague)

    val summary = (dashState as? DashboardUiState.Loaded)?.summary

    LaunchedEffect(Unit) {
        if (analysisVm.uiState.value is AnalysisUiState.Idle) {
            analysisVm.compute(selectedLeague, homeTeam, awayTeam, summary?.kellyFraction ?: 0.25)
        }
    }

    fun onMatchSelected(l: String, h: Team, a: Team) {
        analysisVm.compute(l, h, a, summary?.kellyFraction ?: 0.25)
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ── AppBar ────────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_app_logo),
                    contentDescription = "SentinelBet",
                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = 0.5.sp)) { append("SENTINEL") }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary,      fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = 0.5.sp)) { append("BET") }
                    }
                )
                Spacer(Modifier.weight(1f))
                // Settings icon — libera espaço na nav bar
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = "Configurações", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
                }
            }
        }

        // ── Match selector (hero no topo) ─────────────────────────────────
        item {
            SbCard {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.SportsSoccer, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("CONFIGURAR PARTIDA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.2.sp, fontWeight = FontWeight.Bold))
                    }
                    SbDropdown(
                        label     = "LIGA",
                        selected  = selectedLeague,
                        options   = LEAGUES,
                        itemLabel = { it },
                        onSelected = { l ->
                            analysisVm.selectLeague(l)
                            val t = getTeamsForLeague(l)
                            onMatchSelected(l, t.first(), if (t.size > 1) t[1] else t.first())
                        },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SbDropdown(
                            label     = "CASA",
                            selected  = homeTeam,
                            options   = teams,
                            itemLabel = { it.name },
                            onSelected = { t -> analysisVm.selectHomeTeam(t); onMatchSelected(selectedLeague, t, awayTeam) },
                            modifier  = Modifier.weight(1f),
                        )
                        SbDropdown(
                            label     = "FORA",
                            selected  = awayTeam,
                            options   = teams,
                            itemLabel = { it.name },
                            onSelected = { t -> analysisVm.selectAwayTeam(t); onMatchSelected(selectedLeague, homeTeam, t) },
                            modifier  = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // ── Poisson hero card ─────────────────────────────────────────────
        item {
            when (val s = analysisState) {
                is AnalysisUiState.Ready, is AnalysisUiState.Streaming -> {
                    val a = if (s is AnalysisUiState.Ready) s.analysis else (s as AnalysisUiState.Streaming).analysis
                    PoissonQuickCard(analysis = a)
                }
                is AnalysisUiState.Computing -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Text("Calculando modelo...", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }
                }
                else -> Unit
            }
        }

        // ── KPIs ──────────────────────────────────────────────────────────
        item {
            if (summary != null) {
                val profitColor = if (summary.totalProfit >= 0) Success else Error
                val profitSign  = if (summary.totalProfit >= 0) "+" else ""
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        KpiCard(
                            label      = "Lucro Total",
                            value      = "$profitSign R\$${"%.0f".format(summary.totalProfit)}",
                            subtitle   = "${summary.totalBets} apostas",
                            accentColor= profitColor,
                            icon       = Icons.Filled.TrendingUp,
                            modifier   = Modifier.weight(1f),
                        )
                        KpiCard(
                            label      = "ROI",
                            value      = "${"%.1f".format(summary.roi)}%",
                            subtitle   = "Retorno s/ investido",
                            accentColor= Accent2,
                            icon       = Icons.Filled.Percent,
                            modifier   = Modifier.weight(1f),
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        KpiCard(
                            label      = "Winrate",
                            value      = "${"%.0f".format(summary.winRate * 100)}%",
                            accentColor= Warning,
                            icon       = Icons.Filled.EmojiEvents,
                            modifier   = Modifier.weight(1f),
                        )
                        KpiCard(
                            label      = "Banca Atual",
                            value      = "R\$${"%.0f".format(summary.bankroll)}",
                            subtitle   = "Kelly: ${"%.0f".format(summary.kellyFraction * 100)}%",
                            accentColor= Success,
                            icon       = Icons.Filled.AccountBalanceWallet,
                            modifier   = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // ── Recent bets ───────────────────────────────────────────────────
        item {
            SbCard {
                SbCardHeader(
                    title   = "Histórico Recente",
                    leading = { Icon(Icons.Filled.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp)) },
                )
                if (summary?.recentBets?.isEmpty() != false) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhuma aposta registrada ainda.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                } else {
                    summary.recentBets.forEach { bet -> BetRow(bet = bet) }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ── Dropdown ──────────────────────────────────────────────────────────────

@Composable
fun <T> SbDropdown(
    label: String,
    selected: T,
    options: List<T>,
    itemLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp))
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = itemLabel(selected),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    maxLines = 1,
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(itemLabel(option), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp) },
                    onClick = { onSelected(option); expanded = false },
                )
            }
        }
    }
}

// ── Bet row ───────────────────────────────────────────────────────────────

@Composable
fun BetRow(bet: BetEntity) {
    val color = when (bet.result) {
        BetResult.WIN     -> Success
        BetResult.LOSS    -> Error
        BetResult.PENDING -> Warning
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(bet.match, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold), maxLines = 1)
            Text("${bet.market} · ${"%.2f".format(bet.odd)}x", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        Text(
            "${if (bet.profit >= 0) "+" else ""}R\$${"%.0f".format(bet.profit)}",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color),
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}

// ── Poisson quick card ────────────────────────────────────────────────────

@Composable
private fun PoissonQuickCard(analysis: MatchAnalysis) {
    SbCard {
        // Match header strip
        Box(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(analysis.homeTeam.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp), modifier = Modifier.weight(1f))
                Text("VS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp))
                Text(analysis.awayTeam.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp), modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
            }
        }
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Lambdas
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                LambdaChip("λ CASA",  analysis.lambdaHome,                    MaterialTheme.colorScheme.primary)
                LambdaChip("TOTAL",  analysis.lambdaHome + analysis.lambdaAway, MaterialTheme.colorScheme.onSurfaceVariant)
                LambdaChip("λ FORA", analysis.lambdaAway,                    Accent5)
            }
            // Prob bars
            ProbabilityBar(analysis.homeTeam.name.split(" ").first(), analysis.probHome, MaterialTheme.colorScheme.primary)
            ProbabilityBar("Empate", analysis.probDraw, MaterialTheme.colorScheme.onSurfaceVariant)
            ProbabilityBar(analysis.awayTeam.name.split(" ").first(), analysis.probAway, Accent5)
            // Fair odds
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OddsChip("CASA",   analysis.fairOddHome, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                OddsChip("EMPATE", analysis.fairOddDraw, MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                OddsChip("FORA",   analysis.fairOddAway, Accent5, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun LambdaChip(label: String, value: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text("%.2f".format(value), style = MaterialTheme.typography.titleSmall.copy(color = color, fontWeight = FontWeight.ExtraBold))
    }
}

@Composable
fun OddsChip(label: String, value: Double, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.20f), RoundedCornerShape(6.dp))
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = color.copy(alpha = 0.8f), fontWeight = FontWeight.Bold))
        Text("${"%.2f".format(value)}x", style = MaterialTheme.typography.bodySmall.copy(color = color, fontWeight = FontWeight.ExtraBold))
    }
}
