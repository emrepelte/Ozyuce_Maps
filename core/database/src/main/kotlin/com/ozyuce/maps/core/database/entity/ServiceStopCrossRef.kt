package com.ozyuce.maps.core.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "service_stop_cross_ref",
    primaryKeys = ["serviceId", "stopId"],
    indices = [
        Index(value = ["stopId"])
    ]
)
data class ServiceStopCrossRef(
    val serviceId: String,
    val stopId: String,
    val sequence: Int
)
