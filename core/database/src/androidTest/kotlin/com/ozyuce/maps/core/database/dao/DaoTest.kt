package com.ozyuce.maps.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ozyuce.maps.core.database.OzyuceDatabase
import com.ozyuce.maps.core.database.entity.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DaoTest {
    private lateinit var db: OzyuceDatabase
    private lateinit var personDao: PersonDao
    private lateinit var stopDao: StopDao
    private lateinit var serviceDao: ServiceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            OzyuceDatabase::class.java
        ).build()
        personDao = db.personDao()
        stopDao = db.stopDao()
        serviceDao = db.serviceDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadPerson() = runBlocking {
        val person = PersonEntity(
            id = "1",
            name = "Test Person",
            department = "Test Department",
            avatarUrl = null
        )
        personDao.insert(person)
        val loaded = personDao.getAll().first()
        assertEquals(1, loaded.size)
        assertEquals(person, loaded[0])
    }

    @Test
    fun insertAndReadStop() = runBlocking {
        val stop = StopEntity(
            id = "1",
            name = "Test Stop",
            latitude = 41.0,
            longitude = 29.0,
            etaMinutes = 10
        )
        stopDao.insert(stop)
        val loaded = stopDao.getAll().first()
        assertEquals(1, loaded.size)
        assertEquals(stop, loaded[0])
    }

    @Test
    fun insertAndReadService() = runBlocking {
        val service = ServiceEntity(
            id = "1",
            routeName = "Test Route",
            vehiclePlate = "34 ABC 123",
            startedAt = System.currentTimeMillis(),
            endedAt = null
        )
        serviceDao.insert(service)
        val loaded = serviceDao.getAll().first()
        assertEquals(1, loaded.size)
        assertEquals(service, loaded[0])
    }

    @Test
    fun testServiceStopRelation() = runBlocking {
        val service = ServiceEntity(
            id = "1",
            routeName = "Test Route",
            vehiclePlate = "34 ABC 123",
            startedAt = System.currentTimeMillis(),
            endedAt = null
        )
        val stop = StopEntity(
            id = "1",
            name = "Test Stop",
            latitude = 41.0,
            longitude = 29.0,
            etaMinutes = 10
        )
        val crossRef = ServiceStopCrossRef(
            serviceId = service.id,
            stopId = stop.id,
            sequence = 1
        )

        serviceDao.insert(service)
        stopDao.insert(stop)
        serviceDao.insertServiceStopCrossRef(crossRef)

        val stops = stopDao.getStopsByServiceId(service.id).first()
        assertEquals(1, stops.size)
        assertEquals(stop, stops[0])
    }
}
