package com.ozyuce.maps.navigation

/**
 * Navigation rotalar?n? merkezi olarak y?neten sealed class
 * Tip g?venli?i ve kolay y?netim i?in kullan?l?r
 */
sealed class Dest(val route: String) {

    // Splash / onboarding
    data object Splash : Dest("splash")

    // Authentication
    data object Login : Dest("login")
    data object Register : Dest("register")
    
    // Main Dashboard
    data object Dashboard : Dest("dashboard")
    
    // Service Management
    data object Service : Dest("service")
    
    // Stops and Personnel
    data object Stops : Dest("stops")
    data object StopDetail : Dest("stop_detail/{stopId}") {
        fun createRoute(stopId: String) = "stop_detail/$stopId"
    }
    
    // Reports
    data object Reports : Dest("reports")
    data object DailyReport : Dest("daily_report/{date}") {
        fun createRoute(date: String) = "daily_report/$date"
    }
    
    // Map
    data object Map : Dest("map")
    data object LiveTracking : Dest("live_tracking/{routeId}") {
        fun createRoute(routeId: String) = "live_tracking/$routeId"
    }
    
    // Settings
    data object Settings : Dest("settings")
    data object Profile : Dest("profile")
    
    companion object {
        /**
         * Auth rotalar?n? liste halinde d?ner
         */
        val authRoutes: List<String> by lazy {
            listOf(Login.route, Register.route)
        }
        
        /**
         * Ana ekran rotalar?n? liste halinde d?ner
         */
        val mainRoutes: List<String> by lazy {
            listOf(
                Dashboard.route,
                Service.route,
                Stops.route,
                Reports.route,
                Map.route,
                Settings.route
            )
        }
    }
}
