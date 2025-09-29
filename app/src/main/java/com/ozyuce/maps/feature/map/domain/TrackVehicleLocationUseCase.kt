package com.ozyuce.maps.feature.map.domain

import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.map.domain.model.VehicleLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Ara? konumunu takip eden ve g?ncelleyen use case
 */
class TrackVehicleLocationUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend fun startTracking() {
        withContext(dispatcherProvider.io) {
            mapRepository.startLocationUpdates()
            mapRepository.connectWebSocket()
        }
    }

    suspend fun stopTracking() {
        withContext(dispatcherProvider.io) {
            mapRepository.stopLocationUpdates()
            mapRepository.disconnectWebSocket()
        }
    }

    suspend fun updateLocation(location: LatLng, heading: Float, speed: Float): Result<VehicleLocation> {
        return withContext(dispatcherProvider.io) {
            val result = mapRepository.updateVehicleLocation(location, heading, speed)
            if (result is Result.Success) {
                mapRepository.sendLocationUpdate(result.data)
            }
            result
        }
    }

    fun getLocationFlow(): Flow<VehicleLocation?> {
        return mapRepository.getVehicleLocationFlow()
            .flowOn(dispatcherProvider.io)
    }
}
