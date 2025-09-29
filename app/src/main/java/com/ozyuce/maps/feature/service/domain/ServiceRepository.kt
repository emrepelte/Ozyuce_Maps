package com.ozyuce.maps.feature.service.domain

import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import com.ozyuce.maps.feature.service.domain.model.Route
import kotlinx.coroutines.flow.Flow

/**
 * Servis i?lemleri i?in repository interface
 */
interface ServiceRepository {
    suspend fun startService(routeId: String): Result<ServiceSession>
    suspend fun endService(sessionId: String): Result<ServiceSession>
    suspend fun getCurrentSession(): Result<ServiceSession?>
    fun getCurrentSessionFlow(): Flow<ServiceSession?>
    suspend fun getAvailableRoutes(): Result<List<Route>>
    suspend fun getServiceHistory(): Result<List<ServiceSession>>
}

// ServiceSession model art?k model/ServiceModels.kt dosyas?nda
