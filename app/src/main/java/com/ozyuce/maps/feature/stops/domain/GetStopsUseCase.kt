package com.ozyuce.maps.feature.stops.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.stops.domain.model.Stop
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Rota dura?lar?n? getiren Use Case.
 */
class GetStopsUseCase @Inject constructor(
    private val stopsRepository: StopsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(routeId: String): OzyuceResult<List<Stop>> {
        return withContext(dispatcherProvider.io) {
            if (routeId.isBlank()) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Rota ID gereklidir"))
            }
            
            stopsRepository.getStopsForRoute(routeId)
        }
    }
}
