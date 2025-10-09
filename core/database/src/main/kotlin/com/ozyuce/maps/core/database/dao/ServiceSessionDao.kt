package com.ozyuce.maps.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ozyuce.maps.core.database.entity.ServiceSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * ServiceSession DAO
 */
@Dao
interface ServiceSessionDao {
    
    @Query("SELECT * FROM service_sessions WHERE is_active = 1 LIMIT 1")
    suspend fun getCurrentSession(): ServiceSessionEntity?
    
    @Query("SELECT * FROM service_sessions WHERE is_active = 1 LIMIT 1")
    fun getCurrentSessionFlow(): Flow<ServiceSessionEntity?>
    
    @Query("SELECT * FROM service_sessions WHERE driver_id = :driverId ORDER BY start_time DESC")
    suspend fun getSessionHistory(driverId: String): List<ServiceSessionEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ServiceSessionEntity)
    
    @Update
    suspend fun updateSession(session: ServiceSessionEntity)
    
    @Query("UPDATE service_sessions SET is_active = 0, end_time = :endTime WHERE id = :sessionId")
    suspend fun endSession(sessionId: String, endTime: java.util.Date)
}
