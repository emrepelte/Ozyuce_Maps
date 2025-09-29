package com.ozyuce.maps.core.database.model

data class Service(
    val id: String,
    val routeName: String,
    val vehiclePlate: String,
    val startedAt: Long?,
    val endedAt: Long?
)
