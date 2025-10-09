package com.ozyuce.maps.feature.map.domain

import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.result.OzyuceResult
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
    suspend fun updateVehicleLocation(location: LatLng, heading: Float, speed: Float): OzyuceResult<VehicleLocation>
    fun getVehicleLocationFlow(): Flow<VehicleLocation?>
    suspend fun startLocationUpdates()
    suspend fun stopLocationUpdates()

    // Rota ve duraklar
    suspend fun getRoutePolyline(routeId: String): OzyuceResult<RoutePolyline>
    suspend fun getStopMarkers(routeId: String): OzyuceResult<List<StopMarker>>
    fun getStopMarkersFlow(routeId: String): Flow<List<StopMarker>>

    // ETA hesaplama
    suspend fun calculateEta(origin: LatLng, destination: LatLng): OzyuceResult<RouteEta>
    suspend fun calculateBatchEta(origin: LatLng, destinations: List<LatLng>): OzyuceResult<List<RouteEta>>

    // WebSocket ba?lant?s?
    suspend fun connectWebSocket()
    suspend fun disconnectWebSocket()
    suspend fun sendLocationUpdate(location: VehicleLocation)
}
