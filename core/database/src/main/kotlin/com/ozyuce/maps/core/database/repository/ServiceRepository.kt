package com.ozyuce.maps.core.database.repository

import com.ozyuce.maps.core.database.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getAll(): Flow<List<Service>>
    suspend fun getById(id: String): Service?
    suspend fun insert(service: Service)
    suspend fun insertAll(services: List<Service>)
    suspend fun delete(service: Service)
    suspend fun syncService(id: String)
    suspend fun endService(id: String)
    fun getActiveService(): Flow<Service?>
}
