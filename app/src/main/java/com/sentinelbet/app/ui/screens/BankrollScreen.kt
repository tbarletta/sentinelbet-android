package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.sentinelbet.app.ui.components.*
import com.sentinelbet.app.ui.theme.*
import com.sentinelbet.app.viewmodel.BankrollViewModel
import com.sentinelbet.app.viewmodel.DashboardViewModel
import com.sentinelbet.app.viewmodel.DashboardUiState

@Composable
fun BankrollScreen(
    vm: BankrollViewModel           = hiltViewModel(),
    dashboardVm: DashboardViewModel = hiltViewModel(),
) {
    val uiState   by vm.uiState.collectAsStateWithLifecycle()
    val dashState by dashboardVm.uiState.collectAsStateWithLifecycle()
    val summary   = (dashState as? DashboardUiState.Loaded)?.summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            "BANCA",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onBackground,
            ),
        )

        // ── Kelly Calculator ─────────────────────────────────────────────
        SbCard {
            SbCardHeader(
                title   = "Calculadora Kelly Criterion",
                leading = { Icon(Icons.Filled.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) },
            )
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SbTextField(
                    value         = uiState.bankrollInput,
                    onValueChange = vm::onBankrollChange,
                    label         = "Banca Total (R$)",
                    keyboardType  = KeyboardType.Decimal,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SbTextField(value = uiState.oddInput,  onValueChange = vm::onOddChange,  label = "Odd",      keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    SbTextField(value = uiState.probInput, onValueChange = vm::onProbChange, label = "Prob (%)", keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }
                SbTextField(
                    value         = uiState.kellyFractionInput,
                    onValueChange = vm::onFractionChange,
                    label         = "Fração Kelly (%)",
                    keyboardType  = KeyboardType.Decimal,
                )

                // ── Result (destaque visual) ─────────────────────────────
                val result = uiState.result
                if (result != null) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    if (result.hasValue) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text("STAKE RECOMENDADO", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.5.sp))
                            Text(
                                "R\$${"%.2f".format(result.stake)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary),
                            )
                            Text(
                                "${"%.2f".format(result.kellyPct)}% da banca",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp),
                            )
                        }
                    } else {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Error.copy(alpha = 0.08f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Filled.Block, contentDescription = null, tint = Error, modifier = Modifier.size(24.dp))
                                Text("SEM VALOR NESTA APOSTA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Error, letterSpacing = 1.sp))
                                Text("A odd não oferece edge positivo. Não aposte.", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp))
                            }
                        }
                    }
                }
            }
        }

        // ── Bankroll Guide ───────────────────────────────────────────────
        SbCard {
            SbCardHeader(
                title   = "Guia de Gestão de Banca",
                leading = { Icon(Icons.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp)) },
            )
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GuideRow("🏆 Regra de Ouro", "Nunca exceda 10% da banca por aposta. Use Kelly Fracionado (25%) como padrão para proteger contra variância.", MaterialTheme.colorScheme.primary)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                GuideRow("Micro (1%)",       "Apostas exploratórias, odds incertas",     MaterialTheme.colorScheme.onSurfaceVariant)
                GuideRow("Conservador (2%)", "Edge baixo (3–6%), risco controlado",      Info)
                GuideRow("Moderado (5%)",    "Edge médio (7–12%), alta confiança",        Warning)
                GuideRow("Agressivo (10%)",  "Edge alto (>12%), máxima convicção",        Error)
            }
        }
    }
}

@Composable
private fun GuideRow(title: String, desc: String, color: Color) {
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = color, fontSize = 13.sp))
            Text(desc,  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp))
        }
    }
}
