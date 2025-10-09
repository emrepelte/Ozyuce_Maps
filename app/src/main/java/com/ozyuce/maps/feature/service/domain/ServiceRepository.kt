package com.ozyuce.maps.feature.service.domain

import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import com.ozyuce.maps.feature.service.domain.model.Route
import kotlinx.coroutines.flow.Flow

/**
 * Servis i?lemleri i?in repository interface
 */
interface ServiceRepository {
    suspend fun startService(routeId: String): OzyuceResult<ServiceSession>
    suspend fun endService(sessionId: String): OzyuceResult<ServiceSession>
    suspend fun getCurrentSession(): OzyuceResult<ServiceSession?>
    fun getCurrentSessionFlow(): Flow<ServiceSession?>
    suspend fun getAvailableRoutes(): OzyuceResult<List<Route>>
    suspend fun getServiceHistory(): OzyuceResult<List<ServiceSession>>
}

// ServiceSession model art?k model/ServiceModels.kt dosyas?nda
