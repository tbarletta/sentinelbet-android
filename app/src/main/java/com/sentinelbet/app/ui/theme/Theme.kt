package com.sentinelbet.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Color Schemes ──────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary            = AccentDark,
    secondary          = Accent2,
    tertiary           = Accent5,
    background         = Bg,
    surface            = Bg1,
    surfaceVariant     = Bg2,
    error              = Error,
    onPrimary          = Color(0xFF020E08),
    onSecondary        = TextPrimary,
    onBackground       = TextPrimary,
    onSurface          = TextPrimary,
    onSurfaceVariant   = TextSecondary,
    outline            = Border,
    outlineVariant     = Border2,
)

private val LightColorScheme = lightColorScheme(
    primary            = Accent,
    secondary          = Accent2,
    tertiary           = Accent5,
    background         = LightBg,
    surface            = LightBg1,
    surfaceVariant     = LightBg2,
    error              = Error,
    onPrimary          = Color(0xFFFFFFFF),
    onSecondary        = LightTextPrimary,
    onBackground       = LightTextPrimary,
    onSurface          = LightTextPrimary,
    onSurfaceVariant   = LightTextSecondary,
    outline            = LightBorder,
    outlineVariant     = LightBorder2,
)

// ── Public Theme Composable ────────────────────────────────────────────────

/**
 * @param darkTheme        true = dark, false = light (pass null to follow system)
 * @param nightShiftActive whether to draw the amber blue-light filter overlay
 * @param nightShiftAlpha  overlay opacity 0f..0.6f (derived from user intensity 0..100)
 */
@Composable
fun SentinelBetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    nightShiftActive: Boolean = false,
    nightShiftAlpha: Float = 0.3f,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val bgColor     = if (darkTheme) Bg else LightBg

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = bgColor.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars    = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
    ) {
        if (nightShiftActive && nightShiftAlpha > 0f) {
            // Draw amber overlay on top of all content using BlendMode.Multiply
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            color      = NightShiftAmber.copy(alpha = nightShiftAlpha),
                            blendMode  = BlendMode.SrcOver,
                        )
                    }
            ) {
                content()
            }
        } else {
            content()
        }
    }
}
