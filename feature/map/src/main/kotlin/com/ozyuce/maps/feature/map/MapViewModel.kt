package com.ozyuce.maps.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.ui.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        loadMockData()
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.ToggleMapStyle -> toggleMapStyle()
            is MapEvent.UpdateVehicleLocation -> updateVehicleLocation(event.location)
            is MapEvent.NavigateToStop -> navigateToStops()
        }
    }

    private fun toggleMapStyle() {
        _uiState.value = _uiState.value.copy(
            isDarkStyle = !_uiState.value.isDarkStyle
        )
    }

    private fun updateVehicleLocation(location: LatLng) {
        _uiState.value = _uiState.value.copy(
            vehicleLocation = location
        )
    }

    private fun navigateToStops() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("stops"))
        }
    }

    private fun loadMockData() {
        val istanbulCenter = LatLng(41.0082, 28.9784)

        _uiState.value = MapUiState(
            vehicleLocation = istanbulCenter,
            routePolyline = listOf(
                LatLng(41.0082, 28.9784),
                LatLng(41.0122, 28.9869),
                LatLng(41.0175, 28.9839)
            ),
            stops = listOf(
                StopUiState(
                    id = "1",
                    name = "Durak 1",
                    location = LatLng(41.0082, 28.9784),
                    eta = "5 dk",
                    status = StopStatus.COMPLETED
                ),
                StopUiState(
                    id = "2",
                    name = "Durak 2",
                    location = LatLng(41.0122, 28.9869),
                    eta = "10 dk",
                    status = StopStatus.NEXT
                ),
                StopUiState(
                    id = "3",
                    name = "Durak 3",
                    location = LatLng(41.0175, 28.9839),
                    eta = "15 dk",
                    status = StopStatus.PENDING
                )
            )
        )
    }
}

data class MapUiState(
    val vehicleLocation: LatLng = LatLng(0.0, 0.0),
    val routePolyline: List<LatLng> = emptyList(),
    val stops: List<StopUiState> = emptyList(),
    val isDarkStyle: Boolean = false
) {
    val nextStops: List<StopUiState>
        get() = stops.filter { it.status != StopStatus.COMPLETED }.take(3)
}

data class StopUiState(
    val id: String,
    val name: String,
    val location: LatLng,
    val eta: String,
    val status: StopStatus
)

enum class StopStatus {
    COMPLETED,
    NEXT,
    PENDING
}

sealed interface MapEvent {
    data object ToggleMapStyle : MapEvent
    data class UpdateVehicleLocation(val location: LatLng) : MapEvent
    data class NavigateToStop(val stopId: String) : MapEvent
}
