package com.ozyuce.maps.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey val id: String,
    val name: String,
    val department: String,
    val avatarUrl: String?,
    val lastSyncedAt: Long? = null,
    val needsSync: Boolean = false
)
