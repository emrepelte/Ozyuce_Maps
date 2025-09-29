package com.ozyuce.maps.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Personel Entity
 */
@Entity(tableName = "personnel")
data class PersonnelEntity(
    @PrimaryKey
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "stop_id")
    val stopId: String,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "has_boarded")
    val hasBoarded: Boolean = false
)
