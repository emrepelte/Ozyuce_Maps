package com.ozyuce.maps.feature.stops.domain.model

import java.util.Date

/**
 * Durak domain modeli
 */
data class Stop(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val sequence: Int, // Rotadaki s?ras?
    val estimatedArrivalTime: String? = null, // HH:mm format?nda
    val actualArrivalTime: Date? = null,
    val isCompleted: Boolean = false,
    val personnelCount: Int = 0,
    val checkedPersonnelCount: Int = 0
) {
    /**
     * Durak tamamlanma y?zdesini d?ner
     */
    fun getCompletionPercentage(): Float {
        return if (personnelCount > 0) {
            (checkedPersonnelCount.toFloat() / personnelCount.toFloat()) * 100f
        } else 0f
    }
    
    /**
     * Durak durumu
     */
    fun getStatus(): StopStatus {
        return when {
            !isCompleted && checkedPersonnelCount == 0 -> StopStatus.PENDING
            !isCompleted && checkedPersonnelCount > 0 -> StopStatus.IN_PROGRESS
            isCompleted -> StopStatus.COMPLETED
            else -> StopStatus.PENDING
        }
    }
}

/**
 * Personel domain modeli
 */
data class Personnel(
    val id: String,
    val name: String,
    val surname: String,
    val fullName: String = "$name $surname",
    val phoneNumber: String? = null,
    val stopId: String,
    val stopName: String,
    val isChecked: Boolean = false,
    val checkTime: Date? = null,
    val checkedBy: String? = null, // S?r?c? ID'si
    val notes: String? = null
)

/**
 * Durak durumu enum'u
 */
enum class StopStatus {
    PENDING,     // Beklemede
    IN_PROGRESS, // Devam ediyor
    COMPLETED    // Tamamland?
}

/**
 * Personel kontrol i?lemi
 */
data class PersonnelCheck(
    val personnelId: String,
    val stopId: String,
    val isChecked: Boolean,
    val checkTime: Date = Date(),
    val checkedBy: String,
    val notes: String? = null
)

/**
 * Yeni personel ekleme modeli
 */
data class AddPersonnelRequest(
    val name: String,
    val surname: String,
    val phoneNumber: String? = null,
    val stopId: String
)
