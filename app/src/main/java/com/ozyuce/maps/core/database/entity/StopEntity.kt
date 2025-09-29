package com.ozyuce.maps.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Durak Entity
 */
@Entity(tableName = "stops")
data class StopEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "sequence")
    val sequence: Int,
    
    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: String,
    
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false
)
