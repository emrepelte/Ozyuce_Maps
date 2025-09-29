package com.ozyuce.maps.feature.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.core.network.NetworkUtils
import com.ozyuce.maps.feature.map.domain.CalculateEtaUseCase
import com.ozyuce.maps.feature.map.domain.GetRouteDetailsUseCase
import com.ozyuce.maps.feature.map.domain.TrackVehicleLocationUseCase
import com.ozyuce.maps.feature.map.domain.model.MapStyle
import com.ozyuce.maps.feature.map.domain.model.MapTrackingMode
import com.ozyuce.maps.feature.map.domain.model.RouteEta
import com.ozyuce.maps.feature.map.domain.model.RoutePolyline
import com.ozyuce.maps.feature.map.domain.model.StopMarker
import com.ozyuce.maps.feature.map.domain.model.VehicleLocation
import com.ozyuce.maps.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getRouteDetailsUseCase: GetRouteDetailsUseCase,
    private val trackVehicleLocationUseCase: TrackVehicleLocationUseCase,
    private val calculateEtaUseCase: CalculateEtaUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapScreenState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadRouteDetails()
        observeVehicleLocation()
    }

    fun onLocationPermissionResult(granted: Boolean) {
        val wasTracking = _uiState.value.isTracking
        _uiState.update { it.copy(isLocationPermissionGranted = granted) }
        if (!granted && wasTracking) {
            stopTracking(showFeedback = false)
        }
    }

    private fun loadRouteDetails() {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.update { it.copy(isLoading = true) }

            val routeId = "route_001" // TODO: Aktif rota bilgisini ServiceRepository'den oku

            when (val result = getRouteDetailsUseCase(routeId)) {
                is Result.Success -> {
                    val (polyline, stops) = result.data
                    _uiState.update { state ->
                        state.copy(
                            routePolyline = polyline,
                            stopMarkers = stops,
                            isLoading = false
                        )
                    }
                    stops.firstOrNull()?.let { stop ->
                        calculateEta(stop.location)
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(UiEvent.ShowSnackbar(NetworkUtils.getErrorMessage(result.exception), isError = true))
                    Timber.e(result.exception, "Error loading route details")
                }
                Result.Loading -> _uiState.update { it.copy(isLoading = true) }
            }

            getRouteDetailsUseCase.getStopMarkersFlow(routeId).collectLatest { stops ->
                _uiState.update { it.copy(stopMarkers = stops) }
            }
        }
    }

    private fun observeVehicleLocation() {
        viewModelScope.launch(dispatcherProvider.io) {
            trackVehicleLocationUseCase.getLocationFlow().collectLatest { location ->
                _uiState.update { it.copy(vehicleLocation = location) }
                location?.let { vehicle ->
                    _uiState.value.selectedStop?.let { stop ->
                        calculateEta(stop.location)
                    }
                }
            }
        }
    }

    fun startTracking() {
        if (_uiState.value.isTracking) return
        viewModelScope.launch(dispatcherProvider.io) {
            if (!_uiState.value.isLocationPermissionGranted) {
                _uiEvent.emit(UiEvent.ShowSnackbar("Konum izni gerekli", isError = true))
                return@launch
            }
            try {
                trackVehicleLocationUseCase.startTracking()
                _uiState.update { it.copy(isTracking = true) }
                _uiEvent.emit(UiEvent.ShowSnackbar("Konum takibi ba?lat?ld?"))
            } catch (e: Exception) {
                _uiState.update { it.copy(isTracking = false) }
                _uiEvent.emit(
                    UiEvent.ShowSnackbar(
                        "Konum takibi ba?lat?lamad?: ${e.message ?: "Bilinmeyen hata"}",
                        isError = true
                    )
                )
                Timber.e(e, "Error starting location tracking")
            }
        }
    }

    fun stopTracking(showFeedback: Boolean = true) {
        if (!_uiState.value.isTracking) return
        viewModelScope.launch(dispatcherProvider.io + NonCancellable) {
            try {
                trackVehicleLocationUseCase.stopTracking()
                if (showFeedback) {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Konum takibi durduruldu"))
                }
            } catch (e: Exception) {
                if (showFeedback) {
                    _uiEvent.emit(
                        UiEvent.ShowSnackbar(
                            "Konum takibi durdurulamad?: ${e.message ?: "Bilinmeyen hata"}",
                            isError = true
                        )
                    )
                }
                Timber.e(e, "Error stopping location tracking")
            } finally {
                _uiState.update { it.copy(isTracking = false) }
            }
        }
    }

    private suspend fun calculateEta(destination: LatLng) {
        _uiState.value.vehicleLocation?.let { vehicle ->
            when (val result = calculateEtaUseCase(vehicle.location, listOf(destination))) {
                is Result.Success -> _uiState.update { it.copy(routeEta = result.data.firstOrNull()) }
                is Result.Error -> Timber.e(result.exception, "Error calculating ETA")
                Result.Loading -> Unit
            }
        }
    }

    fun onStopSelected(stop: StopMarker) {
        _uiState.update { it.copy(selectedStop = stop) }
        viewModelScope.launch(dispatcherProvider.io) {
            calculateEta(stop.location)
        }
    }

    fun onMapStyleChanged(style: MapStyle) {
        _uiState.update { it.copy(mapStyle = style) }
    }

    fun onTrackingModeChanged(mode: MapTrackingMode) {
        _uiState.update { it.copy(trackingMode = mode) }
    }

    override fun onCleared() {
        stopTracking(showFeedback = false)
        super.onCleared()
    }
}

data class MapScreenState(
    val isLoading: Boolean = false,
    val routePolyline: RoutePolyline? = null,
    val stopMarkers: List<StopMarker> = emptyList(),
    val vehicleLocation: VehicleLocation? = null,
    val selectedStop: StopMarker? = null,
    val routeEta: RouteEta? = null,
    val mapStyle: MapStyle = MapStyle.NORMAL,
    val trackingMode: MapTrackingMode = MapTrackingMode.FOLLOW,
    val isTracking: Boolean = false,
    val isLocationPermissionGranted: Boolean = false
)
