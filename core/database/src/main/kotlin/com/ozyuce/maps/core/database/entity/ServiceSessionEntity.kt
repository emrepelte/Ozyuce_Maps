package com.ozyuce.maps.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Servis oturum Entity
 */
@Entity(tableName = "service_sessions")
data class ServiceSessionEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "route_id")
    val routeId: String,
    
    @ColumnInfo(name = "driver_id")
    val driverId: String,
    
    @ColumnInfo(name = "start_time")
    val startTime: Date,
    
    @ColumnInfo(name = "end_time")
    val endTime: Date? = null,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
