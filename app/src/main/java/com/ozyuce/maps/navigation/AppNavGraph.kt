package com.ozyuce.maps.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ozyuce.maps.MainScreen
import com.ozyuce.maps.core.designsystem.theme.OzyuceTheme
import com.ozyuce.maps.feature.auth.ui.LoginScreen
import com.ozyuce.maps.feature.map.presentation.MapScreen
import com.ozyuce.maps.feature.reports.presentation.ReportsScreen
import com.ozyuce.maps.feature.service.ui.ServiceScreen
import com.ozyuce.maps.feature.stops.ui.StopsScreen
import com.ozyuce.maps.feature.splash.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Dest.Splash.route) {
        composable(Dest.Splash.route) {
            OzyuceTheme {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Dest.Dashboard.route) {
                            popUpTo(Dest.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
        }
        composable(Dest.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Dest.Dashboard.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Dest.Login.route) {
                        popUpTo(Dest.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Dest.Service.route) {
            ServiceScreen(navController = navController)
        }
        composable(Dest.Stops.route) {
            StopsScreen(navController = navController)
        }
        composable(Dest.Map.route) {
            val snackbarHostState = remember { SnackbarHostState() }
            MapScreen(navController = navController, snackbarHostState = snackbarHostState)
        }
        composable(Dest.Reports.route) {
            ReportsScreen(navController = navController)
        }
        composable(Dest.Profile.route) {
            Text("Profile (TODO)")
        }
    }
}
