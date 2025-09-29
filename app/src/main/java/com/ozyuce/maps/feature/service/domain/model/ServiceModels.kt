package com.ozyuce.maps.feature.service.domain.model

import java.util.Date

/**
 * Servis oturumu domain modeli
 */
data class ServiceSession(
    val id: String,
    val routeId: String,
    val driverId: String,
    val startTime: Date,
    val endTime: Date? = null,
    val isActive: Boolean = true,
    val totalDistance: Double = 0.0, // km
    val averageSpeed: Double = 0.0, // km/h
    val fuelConsumption: Double? = null, // litre
    val stopsCompleted: Int = 0,
    val totalStops: Int = 0
) {
    /**
     * Servisin devam s?resini dakika cinsinden d?ner
     */
    fun getDurationMinutes(): Long {
        val endTime = this.endTime ?: Date()
        return (endTime.time - startTime.time) / (1000 * 60)
    }
    
    /**
     * Servisin tamamlanma y?zdesini d?ner
     */
    fun getCompletionPercentage(): Float {
        return if (totalStops > 0) {
            (stopsCompleted.toFloat() / totalStops.toFloat()) * 100f
        } else 0f
    }
}

/**
 * Servis durumu
 */
enum class ServiceStatus {
    IDLE,        // Beklemede
    ACTIVE,      // Aktif
    PAUSED,      // Duraklat?ld?
    COMPLETED    // Tamamland?
}

/**
 * Rota bilgisi
 */
data class Route(
    val id: String,
    val name: String,
    val description: String,
    val totalStops: Int,
    val estimatedDuration: Int, // dakika
    val estimatedDistance: Double // km
)

