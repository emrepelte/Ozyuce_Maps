package com.ozyuce.maps.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stops")
data class StopEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val etaMinutes: Int?,
    val lastSyncedAt: Long? = null,
    val needsSync: Boolean = false
)
