package com.ozyuce.maps.feature.map.domain

import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.map.domain.model.RouteEta
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Duraklar i?in ETA hesaplayan use case
 */
class CalculateEtaUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(
        currentLocation: LatLng,
        stopLocations: List<LatLng>
    ): Result<List<RouteEta>> {
        return withContext(dispatcherProvider.io) {
            try {
                if (stopLocations.isEmpty()) {
                    return@withContext Result.Success(emptyList())
                }

                // Tek durak i?in
                if (stopLocations.size == 1) {
                    when (val result = mapRepository.calculateEta(currentLocation, stopLocations.first())) {
                        is Result.Success -> Result.Success(listOf(result.data))
                        is Result.Error -> Result.Error(result.exception)
                        Result.Loading -> Result.Loading
                    }
                } 
                // ?oklu durak i?in batch hesaplama
                else {
                    mapRepository.calculateBatchEta(currentLocation, stopLocations)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}
