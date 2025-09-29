package com.ozyuce.maps.feature.stops.data.repository

import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.stops.domain.StopsRepository
import com.ozyuce.maps.feature.stops.domain.model.Stop
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import com.ozyuce.maps.feature.stops.domain.model.PersonnelCheck
import com.ozyuce.maps.feature.stops.domain.model.AddPersonnelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * StopsRepository implementasyonu - Demo mode
 */
@Singleton
class StopsRepositoryImpl @Inject constructor() : StopsRepository {

    // In-memory storage for demo
    private val _stopsFlow = MutableStateFlow<List<Stop>>(emptyList())
    private val _personnelFlow = MutableStateFlow<List<Personnel>>(emptyList())

    init {
        // Initialize with mock data
        initializeMockData()
    }

    override suspend fun getStopsForRoute(routeId: String): Result<List<Stop>> {
        return try {
            kotlinx.coroutines.delay(500) // Network simulation
            
            val stops = getMockStopsForRoute(routeId)
            _stopsFlow.value = stops
            
            Result.Success(stops)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getPersonnelForStop(stopId: String): Result<List<Personnel>> {
        return try {
            kotlinx.coroutines.delay(300) // Network simulation
            
            val personnel = _personnelFlow.value.filter { it.stopId == stopId }
            
            Result.Success(personnel)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAllPersonnelForRoute(routeId: String): Result<List<Personnel>> {
        return try {
            kotlinx.coroutines.delay(400) // Network simulation
            
            val stops = getMockStopsForRoute(routeId)
            val stopIds = stops.map { it.id }
            val personnel = _personnelFlow.value.filter { it.stopId in stopIds }
            
            Result.Success(personnel)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun checkPersonnel(personnelCheck: PersonnelCheck): Result<Personnel> {
        return try {
            kotlinx.coroutines.delay(600) // Network simulation
            
            val currentPersonnel = _personnelFlow.value.toMutableList()
            val index = currentPersonnel.indexOfFirst { it.id == personnelCheck.personnelId }
            
            if (index == -1) {
                return Result.Error(Exception("Personel bulunamad?"))
            }
            
            val updatedPersonnel = currentPersonnel[index].copy(
                isChecked = personnelCheck.isChecked,
                checkTime = personnelCheck.checkTime,
                checkedBy = personnelCheck.checkedBy,
                notes = personnelCheck.notes
            )
            
            currentPersonnel[index] = updatedPersonnel
            _personnelFlow.value = currentPersonnel
            
            // Update stop completion status
            updateStopCompletionStatus(personnelCheck.stopId)
            
            Result.Success(updatedPersonnel)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addPersonnel(request: AddPersonnelRequest): Result<Personnel> {
        return try {
            kotlinx.coroutines.delay(800) // Network simulation
            
            val newPersonnel = Personnel(
                id = "personnel_${System.currentTimeMillis()}",
                name = request.name,
                surname = request.surname,
                phoneNumber = request.phoneNumber,
                stopId = request.stopId,
                stopName = getStopName(request.stopId),
                isChecked = false,
                checkTime = null,
                checkedBy = null,
                notes = null
            )
            
            val currentPersonnel = _personnelFlow.value.toMutableList()
            currentPersonnel.add(newPersonnel)
            _personnelFlow.value = currentPersonnel
            
            // Update stop personnel count
            updateStopPersonnelCount(request.stopId)
            
            Result.Success(newPersonnel)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun completeStop(stopId: String): Result<Stop> {
        return try {
            kotlinx.coroutines.delay(400) // Network simulation
            
            val currentStops = _stopsFlow.value.toMutableList()
            val index = currentStops.indexOfFirst { it.id == stopId }
            
            if (index == -1) {
                return Result.Error(Exception("Durak bulunamad?"))
            }
            
            val updatedStop = currentStops[index].copy(
                isCompleted = true,
                actualArrivalTime = Date()
            )
            
            currentStops[index] = updatedStop
            _stopsFlow.value = currentStops
            
            Result.Success(updatedStop)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getStopsFlow(routeId: String): Flow<List<Stop>> {
        return _stopsFlow.map { stops ->
            stops.filter { /* Filter logic for route */ true }
        }
    }

    override fun getPersonnelFlow(stopId: String): Flow<List<Personnel>> {
        return _personnelFlow.map { personnel ->
            personnel.filter { it.stopId == stopId }
        }
    }

    private fun initializeMockData() {
        val mockPersonnel = listOf(
            Personnel("p1", "Ahmet", "Y?lmaz", stopId = "stop_1", stopName = "Merkez Kamp?s", phoneNumber = "555-0101"),
            Personnel("p2", "Fatma", "Kaya", stopId = "stop_1", stopName = "Merkez Kamp?s", phoneNumber = "555-0102"),
            Personnel("p3", "Mehmet", "Demir", stopId = "stop_2", stopName = "M?hendislik Fak?ltesi", phoneNumber = "555-0103"),
            Personnel("p4", "Ay?e", "?ahin", stopId = "stop_2", stopName = "M?hendislik Fak?ltesi"),
            Personnel("p5", "Ali", "?zt?rk", stopId = "stop_3", stopName = "??renci Yurtlar?", phoneNumber = "555-0105"),
            Personnel("p6", "Zeynep", "Arslan", stopId = "stop_3", stopName = "??renci Yurtlar?", phoneNumber = "555-0106"),
            Personnel("p7", "Mustafa", "Y?ld?z", stopId = "stop_4", stopName = "?ehir Merkezi"),
            Personnel("p8", "Elif", "Ceylan", stopId = "stop_4", stopName = "?ehir Merkezi", phoneNumber = "555-0108")
        )
        _personnelFlow.value = mockPersonnel
    }

    private fun getMockStopsForRoute(routeId: String): List<Stop> {
        return when (routeId) {
            "route_1" -> listOf(
                Stop(
                    id = "stop_1",
                    name = "Merkez Kamp?s",
                    description = "Ana kamp?s giri?i",
                    latitude = 39.9334,
                    longitude = 32.8597,
                    sequence = 1,
                    estimatedArrivalTime = "08:30",
                    personnelCount = 2,
                    checkedPersonnelCount = 0
                ),
                Stop(
                    id = "stop_2",
                    name = "M?hendislik Fak?ltesi",
                    description = "M?hendislik binas? ?n?",
                    latitude = 39.9350,
                    longitude = 32.8610,
                    sequence = 2,
                    estimatedArrivalTime = "08:35",
                    personnelCount = 2,
                    checkedPersonnelCount = 0
                ),
                Stop(
                    id = "stop_3",
                    name = "??renci Yurtlar?",
                    description = "Yurt kompleksi",
                    latitude = 39.9370,
                    longitude = 32.8630,
                    sequence = 3,
                    estimatedArrivalTime = "08:45",
                    personnelCount = 2,
                    checkedPersonnelCount = 0
                ),
                Stop(
                    id = "stop_4",
                    name = "?ehir Merkezi",
                    description = "Ana durak",
                    latitude = 39.9400,
                    longitude = 32.8650,
                    sequence = 4,
                    estimatedArrivalTime = "09:00",
                    personnelCount = 2,
                    checkedPersonnelCount = 0
                )
            )
            else -> emptyList()
        }
    }

    private fun updateStopCompletionStatus(stopId: String) {
        val personnel = _personnelFlow.value.filter { it.stopId == stopId }
        val checkedCount = personnel.count { it.isChecked }
        
        val currentStops = _stopsFlow.value.toMutableList()
        val index = currentStops.indexOfFirst { it.id == stopId }
        
        if (index != -1) {
            val updatedStop = currentStops[index].copy(
                checkedPersonnelCount = checkedCount
            )
            currentStops[index] = updatedStop
            _stopsFlow.value = currentStops
        }
    }

    private fun updateStopPersonnelCount(stopId: String) {
        val personnel = _personnelFlow.value.filter { it.stopId == stopId }
        val totalCount = personnel.size
        val checkedCount = personnel.count { it.isChecked }
        
        val currentStops = _stopsFlow.value.toMutableList()
        val index = currentStops.indexOfFirst { it.id == stopId }
        
        if (index != -1) {
            val updatedStop = currentStops[index].copy(
                personnelCount = totalCount,
                checkedPersonnelCount = checkedCount
            )
            currentStops[index] = updatedStop
            _stopsFlow.value = currentStops
        }
    }

    private fun getStopName(stopId: String): String {
        return when (stopId) {
            "stop_1" -> "Merkez Kamp?s"
            "stop_2" -> "M?hendislik Fak?ltesi"
            "stop_3" -> "??renci Yurtlar?"
            "stop_4" -> "?ehir Merkezi"
            else -> "Bilinmeyen Durak"
        }
    }
}
