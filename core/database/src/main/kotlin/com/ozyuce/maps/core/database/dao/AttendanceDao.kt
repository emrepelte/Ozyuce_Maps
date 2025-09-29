package com.ozyuce.maps.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ozyuce.maps.core.database.entity.AttendanceEntity
import com.ozyuce.maps.core.database.entity.AttendanceStatus

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(attendance: AttendanceEntity)

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Query("SELECT COUNT(*) FROM attendance WHERE status = 'LATE' AND date BETWEEN :start AND :end")
    suspend fun countLate(start: Long, end: Long): Int

    @Query("UPDATE attendance SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: AttendanceStatus)
}

