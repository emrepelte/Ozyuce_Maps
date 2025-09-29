package com.ozyuce.maps.feature.map.domain.model

import com.google.android.gms.maps.model.LatLng
import java.util.Date

/**
 * Ara? konumu domain modeli
 */
data class VehicleLocation(
    val id: String,
    val routeId: String,
    val driverId: String,
    val location: LatLng,
    val heading: Float,
    val speed: Float,
    val timestamp: Date,
    val isActive: Boolean = true
)

/**
 * Rota domain modeli
 */
data class RoutePolyline(
    val routeId: String,
    val points: List<LatLng>,
    val distance: Double, // km
    val duration: Int, // dakika
    val trafficDuration: Int? = null // dakika
)

/**
 * Durak domain modeli (harita i?in)
 */
data class StopMarker(
    val id: String,
    val name: String,
    val location: LatLng,
    val sequence: Int,
    val scheduledTime: String,
    val isCompleted: Boolean = false,
    val completedTime: Date? = null,
    val personnelCount: Int = 0,
    val boardedCount: Int = 0
) {
    fun getProgress(): Float {
        if (personnelCount == 0) return 0f
        return boardedCount.toFloat() / personnelCount.toFloat()
    }
}

/**
 * ETA (Estimated Time of Arrival) domain modeli
 */
data class RouteEta(
    val stopId: String,
    val estimatedArrival: Date,
    val distance: Double, // km
    val duration: Int, // dakika
    val trafficDuration: Int? = null // dakika
)

/**
 * Harita durumu enum'?
 */
enum class MapTrackingMode {
    FOLLOW, // Arac? takip et
    FREE, // Serbest hareket
    OVERVIEW // T?m rotay? g?ster
}

/**
 * Harita stil se?enekleri
 */
enum class MapStyle {
    NORMAL,
    SATELLITE,
    TERRAIN,
    HYBRID
}
