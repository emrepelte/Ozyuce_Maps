package com.ozyuce.maps.feature.service.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Start service request DTO
 */
@JsonClass(generateAdapter = true)
data class StartServiceRequestDto(
    @Json(name = "route_id")
    val routeId: String
)

/**
 * Service session DTO
 */
@JsonClass(generateAdapter = true)
data class ServiceSessionDto(
    @Json(name = "id")
    val id: String,
    @Json(name = "route_id")
    val routeId: String,
    @Json(name = "driver_id")
    val driverId: String,
    @Json(name = "start_time")
    val startTime: String, // ISO 8601 format
    @Json(name = "end_time")
    val endTime: String?, // ISO 8601 format, nullable
    @Json(name = "is_active")
    val isActive: Boolean
)
