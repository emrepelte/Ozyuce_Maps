package com.ozyuce.maps.feature.stops.domain

import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.stops.domain.model.Stop
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import com.ozyuce.maps.feature.stops.domain.model.PersonnelCheck
import com.ozyuce.maps.feature.stops.domain.model.AddPersonnelRequest
import kotlinx.coroutines.flow.Flow

/**
 * Durak i?lemleri i?in repository interface
 */
interface StopsRepository {
    suspend fun getStopsForRoute(routeId: String): OzyuceResult<List<Stop>>
    suspend fun getPersonnelForStop(stopId: String): OzyuceResult<List<Personnel>>
    suspend fun getAllPersonnelForRoute(routeId: String): OzyuceResult<List<Personnel>>
    suspend fun checkPersonnel(personnelCheck: PersonnelCheck): OzyuceResult<Personnel>
    suspend fun addPersonnel(request: AddPersonnelRequest): OzyuceResult<Personnel>
    suspend fun completeStop(stopId: String): OzyuceResult<Stop>
    fun getStopsFlow(routeId: String): Flow<List<Stop>>
    fun getPersonnelFlow(stopId: String): Flow<List<Personnel>>
}

// Modeller art?k model/StopsModels.kt dosyas?nda
