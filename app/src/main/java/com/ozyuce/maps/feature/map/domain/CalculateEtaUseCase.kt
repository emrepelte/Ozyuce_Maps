package com.ozyuce.maps.feature.map.domain

import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
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
    ): OzyuceResult<List<RouteEta>> = withContext(dispatcherProvider.io) {
        try {
            when {
                stopLocations.isEmpty() -> OzyuceResult.Success(emptyList())
                stopLocations.size == 1 -> {
                    when (val result = mapRepository.calculateEta(currentLocation, stopLocations.first())) {
                        is OzyuceResult.Success -> OzyuceResult.Success(listOf(result.data))
                        is OzyuceResult.Error -> OzyuceResult.Error(result.exception)
                        OzyuceResult.Loading -> OzyuceResult.Loading
                    }
                }
                else -> mapRepository.calculateBatchEta(currentLocation, stopLocations)
            }
        } catch (e: Exception) {
            OzyuceResult.Error(e)
        }
    }
}
