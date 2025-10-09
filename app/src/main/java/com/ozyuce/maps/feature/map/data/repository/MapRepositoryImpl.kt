package com.ozyuce.maps.feature.map.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.Constants
import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.core.location.LocationManager
import com.ozyuce.maps.core.network.websocket.LocationWebSocketClient
import com.ozyuce.maps.feature.map.domain.MapRepository
import com.ozyuce.maps.feature.map.domain.model.RouteEta
import com.ozyuce.maps.feature.map.domain.model.RoutePolyline
import com.ozyuce.maps.feature.map.domain.model.StopMarker
import com.ozyuce.maps.feature.map.domain.model.VehicleLocation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepositoryImpl @Inject constructor(
    private val locationManager: LocationManager,
    private val webSocketClient: LocationWebSocketClient,
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
) : MapRepository {

    private val userIdKey = stringPreferencesKey(Constants.USER_ID_KEY)
    private val routeIdKey = stringPreferencesKey(Constants.ROUTE_ID_KEY)

    private val _vehicleLocation = MutableStateFlow<VehicleLocation?>(null)
    private val _stopMarkers = MutableStateFlow<List<StopMarker>>(emptyList())

    private val locationScope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
    private var locationJob: Job? = null

    override suspend fun updateVehicleLocation(
        location: LatLng,
        heading: Float,
        speed: Float
    ): OzyuceResult<VehicleLocation> {
        return try {
            val preferences = dataStore.data.first()
            val userId = preferences[userIdKey] ?: throw IllegalStateException("User ID not found")
            val routeId = preferences[routeIdKey] ?: throw IllegalStateException("Route ID not found")

            val vehicleLocation = VehicleLocation(
                id = UUID.randomUUID().toString(),
                routeId = routeId,
                driverId = userId,
                location = location,
                heading = heading,
                speed = speed,
                timestamp = Date()
            )
            _vehicleLocation.value = vehicleLocation
            OzyuceResult.Success(vehicleLocation)
        } catch (e: Exception) {
            Timber.e(e, "Ara? konumu g?ncellenemedi")
            OzyuceResult.Error(e)
        }
    }

    override fun getVehicleLocationFlow(): Flow<VehicleLocation?> = _vehicleLocation

    override suspend fun startLocationUpdates() {
        locationManager.startLocationUpdates()
        if (locationJob?.isActive == true) return

        locationJob = locationScope.launch {
            locationManager.getLocationFlow()
                .catch { throwable ->
                    if (throwable !is CancellationException) {
                        Timber.e(throwable, "Konum ak??? hata verdi")
                    }
                }
                .collectLatest { update ->
                    when (val result = updateVehicleLocation(update.location, update.heading, update.speed)) {
                        is OzyuceResult.Success -> runCatching {
                            webSocketClient.sendLocation(result.data)
                        }.onFailure { error ->
                            Timber.e(error, "Konum WebSocket'e iletilemedi")
                        }
                        is OzyuceResult.Error -> Timber.e(result.exception, "Ara? konumu StateFlow'a yaz?lamad?")
                        OzyuceResult.Loading -> Unit
                    }
                }
        }
    }

    override suspend fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
        locationManager.stopLocationUpdates()
    }

    override suspend fun getRoutePolyline(routeId: String): OzyuceResult<RoutePolyline> {
        return OzyuceResult.Success(
            RoutePolyline(
                routeId = routeId,
                points = listOf(
                    LatLng(41.0082, 28.9784),
                    LatLng(41.0200, 29.0000),
                    LatLng(41.0400, 29.0200)
                ),
                distance = 35.5,
                duration = 90
            )
        )
    }

    override suspend fun getStopMarkers(routeId: String): OzyuceResult<List<StopMarker>> {
        val mockStops = listOf(
            StopMarker(
                id = "stop_001",
                name = "Merkez Durak",
                location = LatLng(41.0082, 28.9784),
                sequence = 1,
                scheduledTime = "08:00",
                personnelCount = 5,
                boardedCount = 3
            ),
            StopMarker(
                id = "stop_002",
                name = "Sanayi Sitesi",
                location = LatLng(41.0200, 29.0000),
                sequence = 2,
                scheduledTime = "08:30",
                personnelCount = 4,
                boardedCount = 0
            ),
            StopMarker(
                id = "stop_003",
                name = "?niversite Kamp?s?",
                location = LatLng(41.0400, 29.0200),
                sequence = 3,
                scheduledTime = "09:00",
                personnelCount = 3,
                boardedCount = 0
            )
        )
        _stopMarkers.value = mockStops
        return OzyuceResult.Success(mockStops)
    }

    override fun getStopMarkersFlow(routeId: String): Flow<List<StopMarker>> = _stopMarkers

    override suspend fun calculateEta(origin: LatLng, destination: LatLng): OzyuceResult<RouteEta> {
        return OzyuceResult.Success(
            RouteEta(
                stopId = "mock_stop",
                estimatedArrival = Date(System.currentTimeMillis() + 30 * 60 * 1000),
                distance = 15.5,
                duration = 30,
                trafficDuration = 35
            )
        )
    }

    override suspend fun calculateBatchEta(
        origin: LatLng,
        destinations: List<LatLng>
    ): OzyuceResult<List<RouteEta>> {
        return OzyuceResult.Success(
            destinations.mapIndexed { index, _ ->
                RouteEta(
                    stopId = "mock_stop_$index",
                    estimatedArrival = Date(System.currentTimeMillis() + (index + 1) * 30 * 60 * 1000),
                    distance = 15.5 * (index + 1),
                    duration = 30 * (index + 1),
                    trafficDuration = 35 * (index + 1)
                )
            }
        )
    }

    override suspend fun connectWebSocket() {
        webSocketClient.connect()
    }

    override suspend fun disconnectWebSocket() {
        webSocketClient.disconnect()
    }

    override suspend fun sendLocationUpdate(location: VehicleLocation) {
        webSocketClient.sendLocation(location)
    }
}
