package com.example.rotibox.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = BrownPrimary,
    onPrimary = OnPrimary,
    primaryContainer = BrownPrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = CreamSecondary,
    onSecondary = OnSecondary,
    secondaryContainer = OrangeAccentLight,
    onSecondaryContainer = OnSecondary,
    tertiary = OrangeAccent,
    onTertiary = OnPrimary,
    background = BackgroundLight,
    onBackground = OnBackground,
    surface = SurfaceLight,
    onSurface = OnSurface,
    error = ErrorRed,
    onError = OnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = BrownPrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = BrownPrimaryVariantDark,
    onPrimaryContainer = OnPrimaryDark,
    secondary = CreamSecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = OrangeAccentDark,
    onSecondaryContainer = OnSecondaryDark,
    tertiary = OrangeAccentDark,
    onTertiary = OnPrimaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = ErrorRed,
    onError = OnPrimary
)

@Composable
fun RotiBoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
