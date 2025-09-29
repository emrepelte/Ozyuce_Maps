package com.ozyuce.maps.core.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    System,
    Light,
    Dark
}

private val LightColorScheme = lightColorScheme(
    primary = OzyuceColors.Primary,
    secondary = OzyuceColors.Secondary,
    tertiary = OzyuceColors.Tertiary,
    error = OzyuceColors.Error,
    surface = OzyuceColors.LightSurface,
    background = OzyuceColors.LightBackground,
    onPrimary = OzyuceColors.LightOnPrimary,
    onSecondary = OzyuceColors.LightOnSecondary,
    onTertiary = OzyuceColors.LightOnTertiary
)

private val DarkColorScheme = darkColorScheme(
    primary = OzyuceColors.Primary,
    secondary = OzyuceColors.Secondary,
    tertiary = OzyuceColors.Tertiary,
    error = OzyuceColors.Error,
    surface = OzyuceColors.DarkSurface,
    background = OzyuceColors.DarkBackground,
    onPrimary = OzyuceColors.DarkOnPrimary,
    onSecondary = OzyuceColors.DarkOnSecondary,
    onTertiary = OzyuceColors.DarkOnTertiary
)

@Composable
fun OzyuceTheme(
    themeMode: ThemeMode = ThemeMode.System,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.System -> isSystemDark
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

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
        typography = OzyuceTypography,
        shapes = OzyuceShapes,
        content = content
    )
}

@Composable
fun OzyuceTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val mode = if (darkTheme) ThemeMode.Dark else ThemeMode.Light
    OzyuceTheme(
        themeMode = mode,
        useDynamicColor = dynamicColor,
        content = content
    )
}
