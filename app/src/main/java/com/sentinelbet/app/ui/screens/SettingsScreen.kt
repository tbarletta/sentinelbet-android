package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinelbet.app.R
import com.sentinelbet.app.domain.entities.SubscriptionPlan
import com.sentinelbet.app.ui.theme.*
import com.sentinelbet.app.viewmodel.AuthState
import com.sentinelbet.app.viewmodel.AuthViewModel
import com.sentinelbet.app.viewmodel.ThemeMode
import com.sentinelbet.app.viewmodel.ThemeViewModel

@Composable
fun SettingsScreen(
    themeVm: ThemeViewModel,
    authVm: AuthViewModel? = null,
    onBack: () -> Unit = {},
) {
    val state    by themeVm.state.collectAsState()
    val authState = authVm?.authState?.collectAsState()?.value
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text          = stringResource(R.string.settings_title),
                color         = MaterialTheme.colorScheme.onBackground,
                fontSize      = 13.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.5.sp,
            )
        }

        // ── Appearance Card ─────────────────────────────────────────────────
        SettingsCard(title = stringResource(R.string.settings_section_appearance), icon = Icons.Filled.Palette) {
            Text(
                text  = stringResource(R.string.settings_theme_mode),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            ThemeModeSelector(
                current = state.themeMode,
                onSelect = themeVm::setThemeMode,
            )
        }

        // ── Night Shift Card ────────────────────────────────────────────────
        SettingsCard(title = stringResource(R.string.settings_section_night_shift), icon = Icons.Filled.Bedtime) {

            // Master toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = stringResource(R.string.settings_night_shift_enable),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text  = if (state.nightShiftActive)
                            stringResource(R.string.settings_night_shift_on)
                        else
                            stringResource(R.string.settings_night_shift_off),
                        color = if (state.nightShiftActive) NightShiftAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                    )
                }
                Switch(
                    checked  = state.nightShiftEnabled,
                    onCheckedChange = themeVm::setNightShiftEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor       = Color.White,
                        checkedTrackColor       = NightShiftAmber,
                        uncheckedThumbColor     = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor     = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                )
            }

            if (state.nightShiftEnabled) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(Modifier.height(16.dp))

                // Intensity slider
                Text(
                    text  = stringResource(R.string.settings_night_shift_intensity, state.nightShiftIntensity),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(6.dp))
                Slider(
                    value         = state.nightShiftIntensity.toFloat(),
                    onValueChange = { themeVm.setNightShiftIntensity(it.toInt()) },
                    valueRange    = 10f..80f,
                    steps         = 6,
                    colors        = SliderDefaults.colors(
                        thumbColor       = NightShiftAmber,
                        activeTrackColor = NightShiftAmber,
                    ),
                )

                Spacer(Modifier.height(12.dp))

                // Schedule pickers row
                Text(
                    text  = stringResource(R.string.settings_night_shift_schedule),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HourPicker(
                        modifier = Modifier.weight(1f),
                        label    = stringResource(R.string.settings_night_shift_bedtime),
                        hour     = state.nightShiftBedtime,
                        onHour   = themeVm::setNightShiftBedtime,
                        icon     = Icons.Filled.NightlightRound,
                        tint     = NightShiftAmber,
                    )
                    HourPicker(
                        modifier = Modifier.weight(1f),
                        label    = stringResource(R.string.settings_night_shift_wake),
                        hour     = state.nightShiftWake,
                        onHour   = themeVm::setNightShiftWake,
                        icon     = Icons.Filled.WbSunny,
                        tint     = Accent2,
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Manual override button
                OutlinedButton(
                    onClick   = themeVm::toggleNightShiftNow,
                    modifier  = Modifier.fillMaxWidth(),
                    border    = androidx.compose.foundation.BorderStroke(1.dp, NightShiftAmber.copy(alpha = 0.6f)),
                ) {
                    Icon(
                        imageVector = if (state.nightShiftActive) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = null,
                        tint = NightShiftAmber,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text  = if (state.nightShiftActive)
                            stringResource(R.string.settings_night_shift_turn_off_now)
                        else
                            stringResource(R.string.settings_night_shift_turn_on_now),
                        color = NightShiftAmber,
                        fontSize = 13.sp,
                    )
                }
            }
        }

        // ── Account Card ─────────────────────────────────────────────────────
        if (authVm != null) {
            val currentUser = (authState as? AuthState.Authenticated)?.user
            SettingsCard(title = "CONTA", icon = Icons.Filled.AccountCircle) {
                if (currentUser != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.displayName ?: "Usuário",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = currentUser.email ?: "",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                            )
                        }
                        // Plan badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (currentUser.plan == SubscriptionPlan.PRO)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = if (currentUser.plan == SubscriptionPlan.PRO) "PRO" else "FREE",
                                color = if (currentUser.plan == SubscriptionPlan.PRO)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(Modifier.height(10.dp))
                }
                OutlinedButton(
                    onClick   = { authVm.signOut() },
                    modifier  = Modifier.fillMaxWidth(),
                    border    = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    colors    = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sair da conta", fontSize = 13.sp)
                }
            }
        }

        // ── Info Card ───────────────────────────────────────────────────────
        SettingsCard(title = stringResource(R.string.settings_section_about), icon = Icons.Filled.Info) {
            InfoRow(label = stringResource(R.string.settings_app_version), value = "1.0.0")
            InfoRow(label = stringResource(R.string.settings_ai_model), value = "Claude Sonnet 4.6")
            InfoRow(label = stringResource(R.string.settings_odds_source), value = "API-Football v3")
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ── Reusable composables ───────────────────────────────────────────────────

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 14.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text       = title,
                color      = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                letterSpacing = 0.8.sp,
            )
        }
        content()
    }
}

@Composable
private fun ThemeModeSelector(
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    val options = listOf(
        Triple(ThemeMode.System, Icons.Filled.SettingsSuggest, R.string.settings_theme_system),
        Triple(ThemeMode.Light,  Icons.Filled.LightMode,       R.string.settings_theme_light),
        Triple(ThemeMode.Dark,   Icons.Filled.DarkMode,        R.string.settings_theme_dark),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (mode, icon, labelRes) ->
            val selected = current == mode
            val bgColor  = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                           else MaterialTheme.colorScheme.surfaceVariant
            val borderColor = if (selected) MaterialTheme.colorScheme.primary
                              else MaterialTheme.colorScheme.outline

            OutlinedButton(
                onClick   = { onSelect(mode) },
                modifier  = Modifier.weight(1f),
                border    = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                colors    = ButtonDefaults.outlinedButtonColors(containerColor = bgColor),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text     = stringResource(labelRes),
                        fontSize = 11.sp,
                        color    = if (selected) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
private fun HourPicker(
    modifier: Modifier = Modifier,
    label: String,
    hour: Int,
    onHour: (Int) -> Unit,
    icon: ImageVector,
    tint: Color,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, letterSpacing = 0.6.sp)
        }
        Text(
            text       = "%02d:00".format(hour),
            color      = tint,
            fontSize   = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledIconButton(
                onClick  = { onHour((hour - 1 + 24) % 24) },
                modifier = Modifier.size(28.dp),
                colors   = IconButtonDefaults.filledIconButtonColors(containerColor = tint.copy(alpha = 0.2f)),
            ) {
                Icon(Icons.Filled.Remove, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            }
            FilledIconButton(
                onClick  = { onHour((hour + 1) % 24) },
                modifier = Modifier.size(28.dp),
                colors   = IconButtonDefaults.filledIconButtonColors(containerColor = tint.copy(alpha = 0.2f)),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
