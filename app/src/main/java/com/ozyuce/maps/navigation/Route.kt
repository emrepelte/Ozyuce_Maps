package com.ozyuce.maps.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Map
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val showInBottomBar: Boolean = false
) {
    // Ana Rotalar (Bottom Bar)
    data object Dashboard : Route(
        route = "dashboard",
        title = "Ana Sayfa",
        icon = Icons.Rounded.Home,
        showInBottomBar = true
    )

    data object Stops : Route(
        route = "stops",
        title = "Duraklar",
        icon = Icons.Rounded.List,
        showInBottomBar = true
    )

    data object Map : Route(
        route = "map",
        title = "Harita",
        icon = Icons.Rounded.Map,
        showInBottomBar = true
    )

    data object Reports : Route(
        route = "reports",
        title = "Raporlar",
        icon = Icons.Rounded.BarChart,
        showInBottomBar = true
    )

    // Di?er Rotalar
    data object Profile : Route(
        route = "profile",
        title = "Profil"
    )

    data object AddPerson : Route(
        route = "add_person",
        title = "Yeni Personel"
    )

    // Deep Link Rotalar?
    data object Portal : Route(
        route = "portal",
        title = "Portal"
    )

    data object Arvento : Route(
        route = "arvento",
        title = "Arvento"
    )

    companion object {
        fun bottomBarRoutes() = listOf(Dashboard, Stops, Map, Reports)

        fun fromRoute(route: String): Route {
            return when (route) {
                Dashboard.route -> Dashboard
                Stops.route -> Stops
                Map.route -> Map
                Reports.route -> Reports
                Profile.route -> Profile
                AddPerson.route -> AddPerson
                Portal.route -> Portal
                Arvento.route -> Arvento
                else -> Dashboard
            }
        }
    }
}
