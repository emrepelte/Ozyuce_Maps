package com.ozyuce.maps.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ozyuce.maps.core.database.dao.PersonDao
import com.ozyuce.maps.core.database.dao.ServiceDao
import com.ozyuce.maps.core.database.dao.StopDao
import com.ozyuce.maps.core.database.dao.AttendanceDao
import com.ozyuce.maps.core.database.dao.ServiceSessionDao
import com.ozyuce.maps.core.database.entity.*
import com.ozyuce.maps.core.database.util.Converters

@Database(
    entities = [
        PersonEntity::class,
        StopEntity::class,
        ServiceEntity::class,
        ServiceStopCrossRef::class,
        PersonStopCrossRef::class,
        AttendanceEntity::class,
        ServiceSessionEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class OzyuceDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun stopDao(): StopDao
    abstract fun serviceDao(): ServiceDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun serviceSessionDao(): ServiceSessionDao

    companion object {
        const val DATABASE_NAME = "ozyuce.db"
    }
}

