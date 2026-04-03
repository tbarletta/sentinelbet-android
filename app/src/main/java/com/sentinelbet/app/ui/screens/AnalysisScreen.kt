package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sentinelbet.app.domain.entities.MatchAnalysis
import com.sentinelbet.app.domain.entities.LEAGUES
import com.sentinelbet.app.domain.entities.getTeamsForLeague
import com.sentinelbet.app.ui.components.*
import com.sentinelbet.app.ui.theme.*
import com.sentinelbet.app.viewmodel.*

@Composable
fun AnalysisScreen(
    analysisVm: AnalysisViewModel   = hiltViewModel(),
    dashboardVm: DashboardViewModel = hiltViewModel(),
) {
    val state     by analysisVm.uiState.collectAsStateWithLifecycle()
    val dashState by dashboardVm.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showApiDialog by remember { mutableStateOf(false) }

    val summary  = (dashState as? DashboardUiState.Loaded)?.summary
    val bankroll = summary?.bankroll ?: 1000.0
    val kf       = summary?.kellyFraction ?: 0.25

    LaunchedEffect(Unit) {
        if (state is AnalysisUiState.Idle) {
            val teams = getTeamsForLeague(LEAGUES.first())
            analysisVm.compute(LEAGUES.first(), teams.first(), teams[1], kf)
        }
    }

    val analysis = when (state) {
        is AnalysisUiState.Ready     -> (state as AnalysisUiState.Ready).analysis
        is AnalysisUiState.Streaming -> (state as AnalysisUiState.Streaming).analysis
        else                         -> null
    }
    val streamedText = when (state) {
        is AnalysisUiState.Streaming -> (state as AnalysisUiState.Streaming).streamedText
        is AnalysisUiState.Ready     -> (state as AnalysisUiState.Ready).analysis.aiAnalysis
        else                         -> null
    }
    val errorMessage = (state as? AnalysisUiState.Error)?.message
    val isStreaming  = state is AnalysisUiState.Streaming
    val isComputing  = state is AnalysisUiState.Computing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // ── Header ──────────────────────────────────────────────────────
        Text(
            "ANÁLISE IA",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onBackground,
            ),
        )

        // ── Match context strip ─────────────────────────────────────────
        if (analysis != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(analysis.homeTeam.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp), maxLines = 1)
                        Text(analysis.homeTeam.league, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                    }
                    Text("VS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp, fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 8.dp))
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(analysis.awayTeam.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp), maxLines = 1, textAlign = androidx.compose.ui.text.style.TextAlign.End)
                        Text(analysis.awayTeam.league, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                    }
                }
            }
        }

        // ── CTA — Iniciar Análise (proeminente antes da matriz) ─────────
        SbCard {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulseDot(color = if (isStreaming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "CLAUDE SONNET — Análise Preditiva",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.5.sp, color = MaterialTheme.colorScheme.primary),
                    )
                    if (isStreaming) {
                        Spacer(Modifier.weight(1f))
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
                    }
                }

                // Resultado da análise / placeholder
                Box(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            1.dp,
                            if (errorMessage != null) Error.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(8.dp),
                        )
                        .padding(14.dp),
                ) {
                    when {
                        errorMessage != null -> Text(
                            "❌ Erro: $errorMessage",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Error, lineHeight = 20.sp),
                            softWrap = true,
                        )
                        streamedText.isNullOrEmpty() -> Text(
                            if (isStreaming) "Analisando…" else "Toque em \"Iniciar Análise IA\" para obter insights táticos e recomendações desta partida.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp),
                            softWrap = true,
                        )
                        else -> Text(
                            streamedText,
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface, lineHeight = 22.sp),
                            softWrap = true,
                        )
                    }
                }

                // CTA principal — sempre visível
                SbButton(
                    label   = if (isStreaming) "Analisando…" else "Iniciar Análise IA",
                    loading = isStreaming || isComputing,
                    enabled = !isStreaming && !isComputing,
                    icon    = if (!isStreaming && !isComputing) Icons.Filled.AutoAwesome else null,
                    onClick = {
                        val saved = analysisVm.getSavedApiKey()
                        if (saved.isNullOrEmpty()) showApiDialog = true
                        else analysisVm.streamAiAnalysis(saved, bankroll, kf)
                    },
                )
            }
        }

        // ── Poisson Matrix (detalhe técnico, depois do CTA) ─────────────
        if (analysis != null) {
            PoissonMatrixCard(analysis = analysis)
        }

        Spacer(Modifier.height(8.dp))
    }

    if (showApiDialog) {
        ApiKeyDialog(
            onDismiss = { showApiDialog = false },
            onConfirm = { key ->
                showApiDialog = false
                analysisVm.streamAiAnalysis(key, bankroll, kf)
            },
        )
    }
}

// ── Poisson Matrix ────────────────────────────────────────────────────────

@Composable
private fun PoissonMatrixCard(analysis: MatchAnalysis) {
    SbCard {
        SbCardHeader(title = "Matriz de Poisson — Prob. por Placar")
        Column(Modifier.padding(14.dp)) {
            val homeName = analysis.homeTeam.name.split(" ").first()
            val awayName = analysis.awayTeam.name.split(" ").first()
            Text(
                "$homeName (Casa) × $awayName (Fora)",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp),
            )
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.size(32.dp))
                for (col in 0..5) {
                    Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        Text(col.toString(), style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                    }
                }
            }
            for (h in 0..5) {
                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        Text(h.toString(), style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
                    }
                    for (a in 0..5) {
                        val prob = (analysis.poissonMatrix.getOrNull(h)?.getOrNull(a) ?: 0.0) * 100
                        val intensity = (prob / 20.0).coerceIn(0.0, 1.0).toFloat()
                        val bg = MaterialTheme.colorScheme.primary.copy(alpha = intensity * 0.5f + 0.05f)
                        Box(
                            Modifier.size(32.dp).clip(RoundedCornerShape(3.dp)).background(bg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "${"%.1f".format(prob)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 7.5.sp,
                                    color = if (intensity > 0.4) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (intensity > 0.4) FontWeight.Bold else FontWeight.Normal,
                                ),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

// ── API Key Dialog ────────────────────────────────────────────────────────

@Composable
private fun ApiKeyDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var key by remember { mutableStateOf("") }
    var showKey by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text("Chave API Anthropic", style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Insira sua API Key para usar a análise de IA.", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("sk-ant-api03-…", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) },
                    label = { Text("API Key", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) },
                    singleLine = true,
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    leadingIcon = {
                        Icon(Icons.Filled.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
                if (key.isNotBlank() && !key.startsWith("sk-ant-")) {
                    Text("⚠️ A chave deve começar com sk-ant-", style = MaterialTheme.typography.bodySmall.copy(color = Warning, fontSize = 11.sp))
                }
            }
        },
        confirmButton = {
            SbButton(label = "Salvar e Analisar", enabled = key.isNotBlank(), onClick = { if (key.isNotBlank()) onConfirm(key.trim()) }, modifier = Modifier.width(180.dp))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        },
    )
}
