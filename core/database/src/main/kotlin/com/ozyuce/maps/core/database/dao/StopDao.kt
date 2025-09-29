package com.ozyuce.maps.core.database.dao

import androidx.room.*
import com.ozyuce.maps.core.database.entity.StopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StopDao {
    @Query("SELECT * FROM stops")
    fun getAll(): Flow<List<StopEntity>>

    @Query("SELECT * FROM stops WHERE id = :id")
    suspend fun getById(id: String): StopEntity?

    @Query("SELECT * FROM stops WHERE needsSync = 1")
    suspend fun getUnsynced(): List<StopEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stop: StopEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stops: List<StopEntity>)

    @Delete
    suspend fun delete(stop: StopEntity)

    @Query("UPDATE stops SET lastSyncedAt = :timestamp, needsSync = 0 WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)

    @Query("UPDATE stops SET etaMinutes = :etaMinutes WHERE id = :id")
    suspend fun updateEta(id: String, etaMinutes: Int)

    @Query("""
        SELECT * FROM stops
        INNER JOIN service_stop_cross_ref ON stops.id = service_stop_cross_ref.stopId
        WHERE service_stop_cross_ref.serviceId = :serviceId
        ORDER BY service_stop_cross_ref.sequence ASC
    """)
    fun getStopsByServiceId(serviceId: String): Flow<List<StopEntity>>
}
