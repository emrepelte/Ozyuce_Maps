package com.ozyuce.maps.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.ozyuce.maps.core.database.MIGRATION_1_2
import com.ozyuce.maps.core.database.OzyuceDatabase
import com.ozyuce.maps.core.database.dao.AttendanceDao
import com.ozyuce.maps.core.database.dao.PersonDao
import com.ozyuce.maps.core.database.dao.ServiceDao
import com.ozyuce.maps.core.database.dao.ServiceSessionDao
import com.ozyuce.maps.core.database.dao.StopDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ozyuce_maps_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOzyuceDatabase(
        @ApplicationContext context: Context
    ): OzyuceDatabase {
        return Room.databaseBuilder(
            context,
            OzyuceDatabase::class.java,
            OzyuceDatabase.DATABASE_NAME
        ).addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideStopDao(database: OzyuceDatabase): StopDao = database.stopDao()

    @Provides
    fun providePersonDao(database: OzyuceDatabase): PersonDao = database.personDao()

    @Provides
    fun provideServiceDao(database: OzyuceDatabase): ServiceDao = database.serviceDao()

    @Provides
    fun provideAttendanceDao(database: OzyuceDatabase): AttendanceDao = database.attendanceDao()

    @Provides
    fun provideServiceSessionDao(database: OzyuceDatabase): ServiceSessionDao = database.serviceSessionDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore
}
