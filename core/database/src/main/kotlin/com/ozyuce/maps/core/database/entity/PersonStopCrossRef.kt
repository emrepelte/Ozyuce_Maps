package com.ozyuce.maps.core.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "person_stop_cross_ref",
    primaryKeys = ["personId", "stopId"],
    indices = [
        Index(value = ["stopId"])
    ]
)
data class PersonStopCrossRef(
    val personId: String,
    val stopId: String,
    val status: PersonStopStatus,
    val updatedAt: Long
)

enum class PersonStopStatus {
    ON_BOARD,
    ABSENT,
    PENDING
}
