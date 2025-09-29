package com.ozyuce.maps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.ozyuce.maps.core.designsystem.theme.OzyuceTheme
import com.ozyuce.maps.core.designsystem.theme.ThemeMode
import com.ozyuce.maps.navigation.AppNavGraph
import com.ozyuce.maps.navigation.NavigationHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            AppNavGraph(navController = navController)
        }
    }
}


@Composable
fun MainScreen(onLogout: () -> Unit) {
    var themeMode by remember { mutableStateOf(ThemeMode.System) }
    var useDynamicColor by remember { mutableStateOf(false) }

    OzyuceTheme(themeMode = themeMode, useDynamicColor = useDynamicColor) {
        NavigationHost(
            themeMode = themeMode,
            useDynamicColor = useDynamicColor,
            onToggleThemeMode = {
                themeMode = when (themeMode) {
                    ThemeMode.System -> ThemeMode.Light
                    ThemeMode.Light -> ThemeMode.Dark
                    ThemeMode.Dark -> ThemeMode.System
                }
            },
            onToggleDynamicColor = { useDynamicColor = !useDynamicColor },
            onLogout = onLogout
        )
    }
}
