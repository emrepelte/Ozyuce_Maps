package com.ozyuce.maps.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val routeName: String,
    val vehiclePlate: String,
    val startedAt: Long?,
    val endedAt: Long?,
    val lastSyncedAt: Long? = null,
    val needsSync: Boolean = false
)
