package com.ozyuce.maps.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    indices = [
        Index(value = ["personId"]),
        Index(value = ["serviceId"]),
        Index(value = ["date"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val personId: String,
    val serviceId: String,
    val date: Long,
    @ColumnInfo(defaultValue = "PRESENT")
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE
}

