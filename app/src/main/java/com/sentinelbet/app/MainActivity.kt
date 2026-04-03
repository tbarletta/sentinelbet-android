package com.sentinelbet.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.sentinelbet.app.ui.navigation.AppNavHost
import com.sentinelbet.app.ui.theme.SentinelBetTheme
import com.sentinelbet.app.viewmodel.ThemeMode
import com.sentinelbet.app.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeVm: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val themeState by themeVm.state.collectAsState()
            val systemDark  = isSystemInDarkTheme()

            val isDark = when (themeState.themeMode) {
                ThemeMode.Dark   -> true
                ThemeMode.Light  -> false
                ThemeMode.System -> systemDark
            }

            // Night shift alpha: intensity 0-100 mapped to 0.0-0.55f
            val nightAlpha = (themeState.nightShiftIntensity / 100f) * 0.55f

            // Refresh scheduled night shift whenever the composable recomposes
            LaunchedEffect(Unit) { themeVm.refreshNightShiftActive() }

            SentinelBetTheme(
                darkTheme         = isDark,
                nightShiftActive  = themeState.nightShiftActive,
                nightShiftAlpha   = nightAlpha,
            ) {
                AppNavHost()
            }
        }
    }
}
