package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.sentinelbet.app.data.repository.ApiFootballRepository
import com.sentinelbet.app.data.repository.LiveFixture
import com.sentinelbet.app.ui.components.*
import com.sentinelbet.app.ui.theme.*
import com.sentinelbet.app.viewmodel.FixturesUiState
import com.sentinelbet.app.viewmodel.FixturesViewModel

@Composable
fun LiveScreen(vm: FixturesViewModel = hiltViewModel()) {
    val state  by vm.uiState.collectAsStateWithLifecycle()
    val apiKey by vm.apiKey.collectAsStateWithLifecycle()

    var showKeyDialog   by remember { mutableStateOf(false) }
    var selectedLeague  by remember { mutableStateOf("Todos") }

    val leagueOptions = listOf("Todos") + ApiFootballRepository.LEAGUE_IDS.keys.toList()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulseDot(color = androidx.compose.ui.graphics.Color(0xFFFF4444))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "AO VIVO",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { showKeyDialog = true }) {
                        Icon(Icons.Filled.Key, contentDescription = "API Key", tint = if (apiKey.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { vm.loadLiveFixtures() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Atualizar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // ── Filtro de liga ────────────────────────────────────────────────────
        item {
            SbCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "FILTRAR POR LIGA",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SbButton(
                            label   = if (selectedLeague == "Todos") "🌍 Todos" else "🌍 Todos",
                            enabled = true,
                            onClick = {
                                selectedLeague = "Todos"
                                vm.loadLiveFixtures()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        SbButton(
                            label   = "📅 Hoje",
                            enabled = selectedLeague != "Todos",
                            onClick = {
                                if (selectedLeague != "Todos") vm.loadTodayFixtures(selectedLeague)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    SbDropdown(
                        label     = "LIGA",
                        selected  = selectedLeague,
                        options   = leagueOptions,
                        itemLabel = { it },
                        onSelected = { l ->
                            selectedLeague = l
                            if (l == "Todos") vm.loadLiveFixtures()
                            else vm.loadTodayFixtures(l)
                        },
                    )
                }
            }
        }

        // ── Conteúdo principal ────────────────────────────────────────────────
        when (val s = state) {
            is FixturesUiState.Idle -> item {
                EmptyState(
                    icon    = Icons.Filled.SportsSoccer,
                    message = "Toque em Atualizar para buscar jogos ao vivo",
                )
            }

            is FixturesUiState.Loading -> item {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Text("Buscando jogos...", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }
            }

            is FixturesUiState.NoApiKey -> item {
                SbCard {
                    Column(
                        Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(Icons.Filled.Key, contentDescription = null, tint = Warning, modifier = Modifier.size(36.dp))
                        Text(
                            "Configure sua chave API-Football",
                            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold),
                        )
                        Text(
                            "Crie uma conta gratuita em api-sports.io e insira sua chave para ver jogos ao vivo e odds reais.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp),
                        )
                        SbButton(
                            label   = "Configurar chave",
                            icon    = Icons.Filled.Key,
                            onClick = { showKeyDialog = true },
                        )
                    }
                }
            }

            is FixturesUiState.Error -> item {
                SbCard {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            "❌ Erro ao buscar dados",
                            style = MaterialTheme.typography.titleSmall.copy(color = androidx.compose.ui.graphics.Color(0xFFFF5555)),
                        )
                        Text(
                            s.message,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        )
                        SbButton(label = "Tentar novamente", onClick = { vm.refresh() })
                    }
                }
            }

            is FixturesUiState.Loaded -> {
                if (s.fixtures.isEmpty()) {
                    item {
                        EmptyState(
                            icon    = Icons.Filled.SportsScore,
                            message = if (s.isLive) "Nenhum jogo ao vivo agora" else "Nenhum jogo hoje nessa liga",
                        )
                    }
                } else {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                if (s.isLive) "⚡ ${s.fixtures.size} jogos ao vivo"
                                else "📅 ${s.fixtures.size} jogos hoje",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, letterSpacing = 0.5.sp),
                            )
                        }
                    }
                    items(s.fixtures) { fixture ->
                        FixtureCard(fixture = fixture)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    // ── Dialog de API Key ─────────────────────────────────────────────────────
    if (showKeyDialog) {
        ApiFootballKeyDialog(
            currentKey = apiKey,
            onDismiss  = { showKeyDialog = false },
            onConfirm  = { key ->
                vm.saveApiKey(key)
                showKeyDialog = false
                vm.loadLiveFixtures()
            },
        )
    }
}

@Composable
private fun FixtureCard(fixture: LiveFixture) {
    val isLive = fixture.status in listOf("1H", "2H", "HT", "ET", "BT", "P")
    val isFinished = fixture.status == "FT"

    SbCard {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Liga + status
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    fixture.league,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.5.sp),
                    maxLines = 1,
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isLive) {
                        PulseDot(color = androidx.compose.ui.graphics.Color(0xFFFF4444), size = 6.dp)
                    }
                    Text(
                        when {
                            isLive     -> "${fixture.elapsed ?: ""}'"
                            isFinished -> "FT"
                            fixture.status == "NS" -> fixture.date.take(16).replace("T", " ")
                            else       -> fixture.status
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = if (isLive) androidx.compose.ui.graphics.Color(0xFFFF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isLive) FontWeight.Bold else FontWeight.Normal,
                        ),
                    )
                }
            }

            // Times + placar
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Time da casa
                Column(Modifier.weight(1f)) {
                    Text(
                        fixture.homeTeam,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        ),
                        maxLines = 2,
                    )
                }

                // Placar ou vs
                Box(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (fixture.homeGoals != null && fixture.awayGoals != null)
                            "${fixture.homeGoals} — ${fixture.awayGoals}"
                        else "vs",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = if (isLive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                        ),
                    )
                }

                // Time visitante
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        fixture.awayTeam,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        ),
                        maxLines = 2,
                    )
                }
            }

            // Odds (se disponíveis)
            if (fixture.homeOdd != null || fixture.drawOdd != null || fixture.awayOdd != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    OddChip("1", fixture.homeOdd, Accent,                                    Modifier.weight(1f))
                    OddChip("X", fixture.drawOdd, MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                    OddChip("2", fixture.awayOdd, Accent5,                                   Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun OddChip(label: String, odd: Double?, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = color.copy(alpha = 0.7f), fontWeight = FontWeight.Bold))
        Text(
            odd?.let { "${"%.2f".format(it)}" } ?: "—",
            style = MaterialTheme.typography.bodySmall.copy(color = color, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
        )
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), maxLines = 2)
        }
    }
}

@Composable
private fun ApiFootballKeyDialog(
    currentKey: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var key by remember { mutableStateOf(currentKey) }
    var showKey by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                "Chave API-Football",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Crie uma conta gratuita em api-sports.io e cole sua chave abaixo. Plano free: 100 req/dia.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp),
                )
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("sua-chave-aqui", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) },
                    label = { Text("API Key", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) },
                    singleLine = true,
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)) },
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
                        focusedBorderColor    = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor  = MaterialTheme.colorScheme.outline,
                        focusedTextColor      = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor    = MaterialTheme.colorScheme.onSurface,
                        cursorColor           = MaterialTheme.colorScheme.primary,
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
            }
        },
        confirmButton = {
            SbButton(
                label   = "Salvar",
                enabled = key.isNotBlank(),
                onClick = { if (key.isNotBlank()) onConfirm(key.trim()) },
                modifier = Modifier.width(140.dp),
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
    )
}
