package com.ozyuce.maps.core.database.model

data class Stop(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val etaMinutes: Int?
)
