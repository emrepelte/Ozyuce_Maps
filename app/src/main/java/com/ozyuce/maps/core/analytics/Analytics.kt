package com.ozyuce.maps.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Analytics @Inject constructor() {
    private var isEnabled = false
    private val analytics by lazy { Firebase.analytics }
    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    fun initialize(enabled: Boolean) {
        isEnabled = enabled
        analytics.setAnalyticsCollectionEnabled(enabled)
        crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    fun logEvent(name: String, params: Bundle? = null) {
        if (isEnabled) {
            analytics.logEvent(name, params)
        }
    }

    fun logError(throwable: Throwable) {
        if (isEnabled) {
            crashlytics.recordException(throwable)
        }
    }

    fun setUserProperty(name: String, value: String) {
        if (isEnabled) {
            analytics.setUserProperty(name, value)
        }
    }

    companion object {
        // Event isimleri
        const val EVENT_SERVICE_START = "service_start"
        const val EVENT_SERVICE_END = "service_end"
        const val EVENT_STOP_CHECK = "stop_check"
        const val EVENT_ROUTE_DEVIATION = "route_deviation"
        
        // User property'leri
        const val PROP_USER_ROLE = "user_role"
        const val PROP_ACTIVE_ROUTE = "active_route"
    }
}
