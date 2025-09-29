package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.Result as AppResult

interface ServiceRepository {
    suspend fun startService(routeId: String): AppResult<Unit>
    suspend fun endService(): AppResult<Unit>
}