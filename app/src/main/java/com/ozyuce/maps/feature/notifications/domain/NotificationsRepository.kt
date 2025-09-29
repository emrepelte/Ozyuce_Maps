package com.ozyuce.maps.feature.notifications.domain

import com.ozyuce.maps.core.common.result.Result
import com.google.android.gms.maps.model.LatLng

/**
 * Bildirim i?lemleri i?in repository interface
 */
interface NotificationsRepository {
    suspend fun registerFcmToken(token: String): Result<Unit>
    suspend fun subscribeToRoute(routeId: String): Result<Unit>
    suspend fun unsubscribeFromRoute(routeId: String): Result<Unit>
    suspend fun sendRouteDeviationAlert(routeId: String, location: LatLng): Result<Unit>
    suspend fun sendStopSkippedAlert(routeId: String, stopId: String): Result<Unit>
    suspend fun sendDelayAlert(routeId: String, delayMinutes: Int): Result<Unit>
}

/**
 * Bildirim t?rleri
 */
enum class NotificationType {
    ROUTE_DEVIATION,     // Rota sapmas?
    STOP_SKIPPED,        // Durak atland?
    DELAY_ALERT,         // Gecikme uyar?s?
    SERVICE_STARTED,     // Servis ba?lad?
    SERVICE_ENDED,       // Servis bitti
    PERSONNEL_BOARDED,   // Personel bindi
    EMERGENCY_ALERT      // Acil durum
}

/**
 * Bildirim verisi domain modeli
 */
data class NotificationData(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val routeId: String?,
    val stopId: String?,
    val vehicleId: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val priority: NotificationPriority = NotificationPriority.NORMAL
)

/**
 * Bildirim ?ncelik d?zeyi
 */
enum class NotificationPriority {
    LOW, NORMAL, HIGH, CRITICAL
}
