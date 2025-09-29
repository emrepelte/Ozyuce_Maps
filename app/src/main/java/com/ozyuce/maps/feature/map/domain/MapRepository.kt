package com.ozyuce.maps.feature.map.domain

import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.map.domain.model.RouteEta
import com.ozyuce.maps.feature.map.domain.model.RoutePolyline
import com.ozyuce.maps.feature.map.domain.model.StopMarker
import com.ozyuce.maps.feature.map.domain.model.VehicleLocation
import kotlinx.coroutines.flow.Flow

/**
 * Harita i?lemleri i?in repository interface
 */
interface MapRepository {
    // Ara? konumu
    suspend fun updateVehicleLocation(location: LatLng, heading: Float, speed: Float): Result<VehicleLocation>
    fun getVehicleLocationFlow(): Flow<VehicleLocation?>
    suspend fun startLocationUpdates()
    suspend fun stopLocationUpdates()

    // Rota ve duraklar
    suspend fun getRoutePolyline(routeId: String): Result<RoutePolyline>
    suspend fun getStopMarkers(routeId: String): Result<List<StopMarker>>
    fun getStopMarkersFlow(routeId: String): Flow<List<StopMarker>>

    // ETA hesaplama
    suspend fun calculateEta(origin: LatLng, destination: LatLng): Result<RouteEta>
    suspend fun calculateBatchEta(origin: LatLng, destinations: List<LatLng>): Result<List<RouteEta>>

    // WebSocket ba?lant?s?
    suspend fun connectWebSocket()
    suspend fun disconnectWebSocket()
    suspend fun sendLocationUpdate(location: VehicleLocation)
}
