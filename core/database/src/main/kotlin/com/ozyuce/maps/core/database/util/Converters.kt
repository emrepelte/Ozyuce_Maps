package com.ozyuce.maps.core.database.util

import androidx.room.TypeConverter
import com.ozyuce.maps.core.database.entity.PersonStopStatus
import com.ozyuce.maps.core.database.entity.AttendanceStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

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
