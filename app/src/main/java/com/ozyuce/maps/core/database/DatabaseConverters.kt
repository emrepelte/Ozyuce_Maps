package com.ozyuce.maps.core.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room database i?in tip d?n??t?r?c?leri
 */
class DatabaseConverters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
