package com.ozyuce.maps.core.database.dao

import androidx.room.*
import com.ozyuce.maps.core.database.entity.ServiceEntity
import com.ozyuce.maps.core.database.entity.ServiceStopCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services")
    fun getAll(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getById(id: String): ServiceEntity?

    @Query("SELECT * FROM services WHERE needsSync = 1")
    suspend fun getUnsynced(): List<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    @Delete
    suspend fun delete(service: ServiceEntity)

    @Query("UPDATE services SET lastSyncedAt = :timestamp, needsSync = 0 WHERE id = :id")
    suspend fun markSynced(id: String, timestamp: Long)

    @Query("UPDATE services SET endedAt = :timestamp, needsSync = 1 WHERE id = :id")
    suspend fun endService(id: String, timestamp: Long)

    // Stop ili?kileri
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceStopCrossRef(crossRef: ServiceStopCrossRef)

    @Transaction
    @Query("""
        SELECT * FROM services
        WHERE endedAt IS NULL
        ORDER BY startedAt DESC
        LIMIT 1
    """)
    fun getActiveService(): Flow<ServiceEntity?>
}
