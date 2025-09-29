package com.ozyuce.maps.navigation

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ozyuce.maps.core.designsystem.theme.ThemeMode
import com.ozyuce.maps.feature.dashboard.DashboardScreen
import com.ozyuce.maps.feature.map.MapScreen
import com.ozyuce.maps.feature.stops.AddPersonScreen
import com.ozyuce.maps.feature.stops.StopsScreen
import com.ozyuce.maps.ui.components.OzyuceBottomBar
import com.ozyuce.maps.feature.profile.ProfileScreen
import com.ozyuce.maps.feature.profile.components.ProfileDrawer
import com.ozyuce.maps.feature.reports.ReportsScreen
import kotlinx.coroutines.launch

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Dashboard.route,
    themeMode: ThemeMode = ThemeMode.System,
    useDynamicColor: Boolean = false,
    onToggleThemeMode: () -> Unit = {},
    onToggleDynamicColor: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarRoutes = remember { Route.bottomBarRoutes().map { it.route } }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var logoutHandled by remember { mutableStateOf(false) }

    val openCustomTab: (Route) -> Unit = { route ->
        val targetUri = when (route) {
            Route.Portal -> "ozyuce://portal"
            Route.Arvento -> "ozyuce://arvento"
            else -> route.route
        }
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, Uri.parse(targetUri))
    }

    fun navigateToTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun handleNavigation(route: String) {
        when {
            route == Route.Portal.route -> openCustomTab(Route.Portal)
            route == Route.Arvento.route -> openCustomTab(Route.Arvento)
            route == Route.AddPerson.route -> navController.navigate(Route.AddPerson.route)
            route == "passengers" -> navigateToTopLevel(Route.Stops.route)
            route in bottomBarRoutes -> navigateToTopLevel(route)
            route.startsWith(Route.Stops.route) -> navigateToTopLevel(Route.Stops.route)
            else -> navController.navigate(route)
        }
    }

    fun handleNavigation(route: Route) {
        when (route) {
            Route.Portal, Route.Arvento -> openCustomTab(route)
            Route.AddPerson -> navController.navigate(route.route)
            Route.Dashboard, Route.Stops, Route.Map, Route.Reports -> navigateToTopLevel(route.route)
            Route.Profile -> navController.navigate(route.route)
        }
    }

    val shouldShowBottomBar = currentDestination.isBottomBarDestination(bottomBarRoutes)
    
    val openProfileDrawer: () -> Unit = {
        coroutineScope.launch {
            drawerState.open()
        }
    }

    val handleLogoutEvent: () -> Unit = {
        if (!logoutHandled) {
            logoutHandled = true
            coroutineScope.launch { drawerState.close() }
            onLogout()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ProfileDrawer(drawerState = drawerState, onLogout = handleLogoutEvent)
        }
    ) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                if (shouldShowBottomBar) {
                    OzyuceBottomBar(
                        currentDestination = currentDestination,
                        onNavigateToRoute = ::handleNavigation
                    )
                }
            }
        ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Dashboard.route) {
                DashboardScreen(
                    onNavigate = ::handleNavigation,
                    themeMode = themeMode,
                    useDynamicColor = useDynamicColor,
                    onToggleThemeMode = onToggleThemeMode,
                    onToggleDynamicColor = onToggleDynamicColor,
                    onProfileClick = openProfileDrawer
                )
            }
            composable(Route.Stops.route) {
                StopsScreen(
                    onNavigate = ::handleNavigation,
                    onProfileClick = openProfileDrawer
                )
            }
            composable(Route.Map.route) {
                MapScreen(
                    onNavigate = ::handleNavigation,
                    onProfileClick = openProfileDrawer
                )
            }
            composable(Route.Reports.route) {
                ReportsScreen(
                    onProfileClick = openProfileDrawer
                )
            }
            composable(Route.Profile.route) {
                ProfileScreen(onLogout = handleLogoutEvent)
            }
            composable(Route.AddPerson.route) {
                AddPersonScreen(onNavigateUp = { navController.popBackStack() })
            }
        }
    }
    }
}

private fun NavDestination?.isBottomBarDestination(bottomBarRoutes: List<String>): Boolean {
    if (this == null) return false
    return hierarchy.any { destination -> destination.route in bottomBarRoutes }
}

