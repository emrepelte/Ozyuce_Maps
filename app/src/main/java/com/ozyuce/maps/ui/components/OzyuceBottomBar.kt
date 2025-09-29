package com.ozyuce.maps.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ozyuce.maps.navigation.Route

@Composable
fun OzyuceBottomBar(
    currentDestination: NavDestination?,
    onNavigateToRoute: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        Route.bottomBarRoutes().forEach { route ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == route.route
            } ?: false

            NavigationBarItem(
                icon = {
                    route.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = route.title
                        )
                    }
                },
                label = { Text(route.title) },
                selected = selected,
                onClick = { onNavigateToRoute(route) },
                modifier = Modifier.testTag("nav_${route.route}")
            )
        }
    }
}
