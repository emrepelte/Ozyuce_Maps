package com.ozyuce.maps.feature.stops.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Durak personelini getiren Use Case.
 */
class GetPersonnelUseCase @Inject constructor(
    private val stopsRepository: StopsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(stopId: String): OzyuceResult<List<Personnel>> {
        return withContext(dispatcherProvider.io) {
            if (stopId.isBlank()) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Durak ID gereklidir"))
            }
            
            stopsRepository.getPersonnelForStop(stopId)
        }
    }
    
    suspend fun getAllForRoute(routeId: String): OzyuceResult<List<Personnel>> {
        return withContext(dispatcherProvider.io) {
            if (routeId.isBlank()) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Rota ID gereklidir"))
            }
            
            stopsRepository.getAllPersonnelForRoute(routeId)
        }
    }
}
