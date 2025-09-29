package com.ozyuce.maps.data.mapper

import com.ozyuce.maps.core.database.entity.PersonEntity
import com.ozyuce.maps.core.database.entity.StopEntity
import com.ozyuce.maps.domain.repository.Personnel
import com.ozyuce.maps.domain.repository.Stop

fun StopEntity.toDomainStop(sequenceOverride: Int? = null): Stop = Stop(
    id = id,
    name = name,
    sequence = sequenceOverride ?: sequence,
    scheduledTime = runCatching { scheduledTime }.getOrNull()
)

fun Personnel.toEntity(): PersonEntity = PersonEntity(
    id = id,
    name = name,
    department = "",
    avatarUrl = null
)

fun PersonEntity.toDomainPersonnel(active: Boolean = true, stopId: String? = null): Personnel = Personnel(
    id = id,
    name = name,
    active = active,
    stopId = stopId
)