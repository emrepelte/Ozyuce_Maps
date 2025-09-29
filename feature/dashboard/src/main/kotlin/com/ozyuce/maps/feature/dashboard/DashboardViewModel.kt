package com.ozyuce.maps.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.ToggleService -> toggleService()
            is DashboardEvent.DismissWarning -> dismissWarning()
            is DashboardEvent.NavigateToStops -> navigateToStops()
            is DashboardEvent.NavigateToPassengers -> navigateToPassengers()
            is DashboardEvent.NavigateToReports -> navigateToReports()
            is DashboardEvent.OpenPortal -> openPortal()
            is DashboardEvent.OpenArvento -> openArvento()
        }
    }

    private fun toggleService() {
        _uiState.value = _uiState.value.copy(
            isServiceActive = !_uiState.value.isServiceActive
        )
    }

    private fun dismissWarning() {
        _uiState.value = _uiState.value.copy(
            warningMessage = null
        )
    }

    private fun navigateToStops() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("stops"))
        }
    }

    private fun navigateToPassengers() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("passengers"))
        }
    }

    private fun navigateToReports() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("reports"))
        }
    }

    private fun openPortal() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("portal"))
        }
    }

    private fun openArvento() {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Navigate("arvento"))
        }
    }
}

data class DashboardUiState(
    val isServiceActive: Boolean = false,
    val serviceDuration: String = "00:00:00",
    val plateNumber: String = "34 ABC 123",
    val routeName: String = "Hat-1",
    val warningMessage: String? = null
)

sealed interface DashboardEvent {
    data object ToggleService : DashboardEvent
    data object DismissWarning : DashboardEvent
    data object NavigateToStops : DashboardEvent
    data object NavigateToPassengers : DashboardEvent
    data object NavigateToReports : DashboardEvent
    data object OpenPortal : DashboardEvent
    data object OpenArvento : DashboardEvent
}
