package com.ozyuce.maps.core.database.repository

import com.ozyuce.maps.core.database.model.Stop
import kotlinx.coroutines.flow.Flow

interface StopRepository {
    fun getAll(): Flow<List<Stop>>
    suspend fun getById(id: String): Stop?
    suspend fun insert(stop: Stop)
    suspend fun insertAll(stops: List<Stop>)
    suspend fun delete(stop: Stop)
    suspend fun syncStop(id: String)
    suspend fun updateEta(id: String, etaMinutes: Int)
    fun getStopsByServiceId(serviceId: String): Flow<List<Stop>>
}
