package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinelbet.app.data.db.BetEntity
import com.sentinelbet.app.data.db.BetResult
import com.sentinelbet.app.ui.components.*
import com.sentinelbet.app.ui.theme.*
import com.sentinelbet.app.viewmodel.HistoryUiState
import com.sentinelbet.app.viewmodel.HistoryViewModel
import com.sentinelbet.app.viewmodel.PerformanceStats

@Composable
fun HistoryScreen(vm: HistoryViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showForm by remember { mutableStateOf(false) }

    val bets  = (state as? HistoryUiState.Loaded)?.bets  ?: emptyList()
    val perf  = (state as? HistoryUiState.Loaded)?.performance

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("PERFORMANCE", style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface))
                IconButton(onClick = { showForm = !showForm }) {
                    Icon(if (showForm) Icons.Filled.Close else Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Add Bet Form
        if (showForm) {
            item {
                AddBetForm(
                    onSave = { match, market, odd, stake, result, profit, notes ->
                        vm.newBet(match, market, odd, stake, result, profit, notes)
                        showForm = false
                    },
                    onCancel = { showForm = false },
                )
            }
        }

        // KPIs
        if (perf != null) {
            item { PerformanceKpis(perf) }
        }

        // Profit curve (simple text representation — replace with MPAndroidChart if desired)
        if (perf != null && perf.cumulativeProfits.isNotEmpty()) {
            item { ProfitCurveCard(perf) }
        }

        // Bets list
        item {
            SbCard {
                SbCardHeader(
                    title    = "Histórico Completo",
                    trailing = { Text("${bets.size} registros", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)) },
                )
                if (bets.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhuma aposta registrada ainda.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                } else {
                    bets.reversed().forEach { bet ->
                        HistoryBetRow(bet = bet, onDelete = { vm.deleteBet(bet.id) })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun PerformanceKpis(perf: PerformanceStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val profitColor = if (perf.totalProfit >= 0) Success else Error
            KpiCard(
                label = "Lucro Total",
                value = "${if (perf.totalProfit >= 0) "+" else ""}R\$${"%.0f".format(perf.totalProfit)}",
                accentColor = profitColor, icon = Icons.Filled.TrendingUp,
                modifier = Modifier.weight(1f),
            )
            KpiCard(
                label = "ROI",
                value = "${"%.1f".format(perf.roi)}%",
                subtitle = "Retorno s/ investido",
                accentColor = Accent2, icon = Icons.Filled.Percent,
                modifier = Modifier.weight(1f),
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiCard(
                label = "Winrate",
                value = "${"%.0f".format(perf.winRate * 100)}%",
                subtitle = "${perf.wins}W / ${perf.losses}L",
                accentColor = Warning, icon = Icons.Filled.EmojiEvents,
                modifier = Modifier.weight(1f),
            )
            KpiCard(
                label = "Total Apostas",
                value = "${perf.totalBets}",
                accentColor = Accent5, icon = Icons.Filled.BarChart,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfitCurveCard(perf: PerformanceStats) {
    SbCard {
        SbCardHeader(title = "Curva de Lucro")
        Column(Modifier.padding(16.dp)) {
            val profits = perf.cumulativeProfits
            val max     = profits.maxOrNull() ?: 1.0
            val min     = profits.minOrNull() ?: 0.0
            val range   = if (max == min) 1.0 else max - min
            val barHeight = 60.dp
            Row(
                Modifier.fillMaxWidth().height(barHeight),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                profits.takeLast(20).forEach { p ->
                    val normalized = ((p - min) / range).toFloat().coerceIn(0f, 1f)
                    val color = if (p >= 0) Success else Error
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(normalized.coerceAtLeast(0.05f))
                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                            .background(color.copy(alpha = 0.7f)),
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Últimas apostas", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                Text("R\$${"%.1f".format(profits.lastOrNull() ?: 0.0)}", style = MaterialTheme.typography.bodySmall.copy(color = if ((profits.lastOrNull() ?: 0.0) >= 0) Success else Error, fontSize = 9.sp, fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun HistoryBetRow(bet: BetEntity, onDelete: () -> Unit) {
    val color = when (bet.result) {
        BetResult.WIN     -> Success
        BetResult.LOSS    -> Error
        BetResult.PENDING -> Warning
    }
    val resultLabel = when (bet.result) {
        BetResult.WIN     -> "WIN"
        BetResult.LOSS    -> "LOSS"
        BetResult.PENDING -> "PENDENTE"
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(bet.match,  style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface), maxLines = 1)
            Text("${bet.market} · ${"%.2f".format(bet.odd)}x · R\$${"%.0f".format(bet.stake)}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${if (bet.profit >= 0) "+" else ""}R\$${"%.0f".format(bet.profit)}",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color),
            )
            Text(resultLabel, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = color.copy(alpha = 0.7f), letterSpacing = 0.5.sp))
        }
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.DeleteOutline, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}

@Composable
private fun AddBetForm(
    onSave:   (String, String, Double, Double, BetResult, Double, String?) -> Unit,
    onCancel: () -> Unit,
) {
    var match   by remember { mutableStateOf("") }
    var market  by remember { mutableStateOf("") }
    var odd     by remember { mutableStateOf("") }
    var stake   by remember { mutableStateOf("") }
    var result  by remember { mutableStateOf(BetResult.WIN) }
    var notes   by remember { mutableStateOf("") }
    var error   by remember { mutableStateOf<String?>(null) }

    SbCard {
        SbCardHeader(
            title   = "Registrar Nova Aposta",
            leading = { Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) },
        )
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SbTextField(value = match,  onValueChange = { match = it },  label = "Partida (ex: Flamengo vs Palmeiras)")
            SbTextField(value = market, onValueChange = { market = it }, label = "Mercado (ex: Casa, Empate, Over 2.5)")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SbTextField(value = odd,   onValueChange = { odd = it },   label = "Odd",      keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                SbTextField(value = stake, onValueChange = { stake = it }, label = "Stake (R$)",keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
            }
            // Result selector
            Text("Resultado", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(BetResult.WIN to "WIN", BetResult.LOSS to "LOSS", BetResult.PENDING to "PENDENTE").forEach { (r, label) ->
                    val sel = result == r
                    val color = when (r) { BetResult.WIN -> Success; BetResult.LOSS -> Error; BetResult.PENDING -> Warning }
                    FilterChip(
                        selected = sel, onClick = { result = r },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor    = color.copy(alpha = 0.15f),
                            selectedLabelColor        = color,
                            containerColor            = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = sel,
                            selectedBorderColor = color.copy(alpha = 0.4f),
                            borderColor         = MaterialTheme.colorScheme.outline,
                        ),
                    )
                }
            }
            SbTextField(value = notes, onValueChange = { notes = it }, label = "Notas (opcional)")
            if (error != null) Text(error!!, style = MaterialTheme.typography.bodySmall.copy(color = Error, fontSize = 10.sp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onCancel, modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                ) { Text("Cancelar") }
                SbButton(
                    label = "Registrar Aposta",
                    onClick = {
                        val o = odd.toDoubleOrNull()
                        val s = stake.toDoubleOrNull()
                        if (match.isBlank() || market.isBlank() || o == null || s == null) {
                            error = "Preencha todos os campos corretamente."
                            return@SbButton
                        }
                        val profit = when (result) {
                            BetResult.WIN     -> s * (o - 1)
                            BetResult.LOSS    -> -s
                            BetResult.PENDING -> 0.0
                        }
                        onSave(match, market, o, s, result, profit, notes.ifBlank { null })
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
