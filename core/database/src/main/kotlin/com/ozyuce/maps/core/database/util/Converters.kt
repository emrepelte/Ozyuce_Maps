package com.ozyuce.maps.core.database.util

import androidx.room.TypeConverter
import com.ozyuce.maps.core.database.entity.PersonStopStatus
import com.ozyuce.maps.core.database.entity.AttendanceStatus

class Converters {
    @TypeConverter
    fun fromPersonStopStatus(status: PersonStopStatus): String {
        return status.name
    }

    @TypeConverter
    fun toPersonStopStatus(status: String): PersonStopStatus {
        return PersonStopStatus.valueOf(status)
    }

    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String = status.name

    @TypeConverter
    fun toAttendanceStatus(value: String): AttendanceStatus = AttendanceStatus.valueOf(value)
}
