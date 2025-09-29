package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.Result as AppResult

data class Personnel(
    val id: String,
    val name: String,
    val active: Boolean,
    val stopId: String? = null
)

interface PersonnelRepository {
    suspend fun getPersonnel(routeId: String): AppResult<List<Personnel>>
    suspend fun addPersonnel(personnel: Personnel): AppResult<Unit>
}