package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SakaDarkColorScheme = darkColorScheme(
    primary = SakaGold,
    onPrimary = SakaDark,
    primaryContainer = SakaGoldDim,
    onPrimaryContainer = SakaWhite,
    secondary = SakaMatcha,
    onSecondary = SakaWhite,
    secondaryContainer = SakaMatchaDark,
    onSecondaryContainer = SakaMatchaLight,
    tertiary = SakaBrown,
    onTertiary = SakaCream,
    background = SakaDark,
    onBackground = SakaCream,
    surface = SakaDarkSoft,
    onSurface = SakaCream,
    surfaceVariant = SakaDarkCard,
    onSurfaceVariant = SakaCreamDim,
    outline = SakaGoldDim,
    error = SakaRed,
    onError = SakaWhite
)

private val SakaLightColorScheme = lightColorScheme(
    primary = SakaGoldDim,
    onPrimary = SakaWhite,
    primaryContainer = SakaGoldLight,
    onPrimaryContainer = SakaDark,
    secondary = SakaMatchaDark,
    onSecondary = SakaWhite,
    secondaryContainer = SakaMatchaLight,
    onSecondaryContainer = SakaDark,
    tertiary = SakaBrown,
    onTertiary = SakaCream,
    background = SakaCream,
    onBackground = SakaDark,
    surface = SakaWhite,
    onSurface = SakaDark,
    surfaceVariant = SakaCreamDim,
    onSurfaceVariant = SakaDarkSoft,
    outline = SakaGoldDim,
    error = SakaRed,
    onError = SakaWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark for SAKA SPEAK's signature ambient stage atmosphere
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SakaDarkColorScheme else SakaLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
