package com.ozyuce.maps.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.ozyuce.maps.core.database.dao.ServiceSessionDao
import com.ozyuce.maps.core.database.entity.ServiceSessionEntity
import com.ozyuce.maps.core.database.entity.StopEntity
import com.ozyuce.maps.core.database.entity.PersonnelEntity

/**
 * OzyuceMaps ana veritaban?
 * Room database s?n?f? - t?m entity'ler ve DAO'lar burada tan?mlan?r
 */
@Database(
    entities = [
        ServiceSessionEntity::class,
        StopEntity::class,
        PersonnelEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class OzyuceMapsDatabase : RoomDatabase() {
    
    // DAO'lar
    abstract fun serviceSessionDao(): ServiceSessionDao
    // abstract fun stopDao(): StopDao
    // abstract fun personnelDao(): PersonnelDao
    
    companion object {
        const val DATABASE_NAME = "ozyuce_maps_database"
    }
}
