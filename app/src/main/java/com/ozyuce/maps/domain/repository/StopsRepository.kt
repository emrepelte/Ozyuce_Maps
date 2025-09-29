package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.Result as AppResult

data class Stop(
    val id: String,
    val name: String,
    val sequence: Int,
    val scheduledTime: String? = null
)

interface StopsRepository {
    suspend fun getStops(routeId: String): AppResult<List<Stop>>
    suspend fun checkStop(stopId: String, boarded: Boolean): AppResult<Unit>
}