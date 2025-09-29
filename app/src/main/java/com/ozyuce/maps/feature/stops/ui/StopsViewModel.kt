package com.ozyuce.maps.feature.stops.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.Personnel
import com.ozyuce.maps.domain.repository.Stop
import com.ozyuce.maps.domain.usecase.AddPersonnelUseCase
import com.ozyuce.maps.domain.usecase.CheckStopUseCase
import com.ozyuce.maps.domain.usecase.GetPersonnelUseCase
import com.ozyuce.maps.domain.usecase.GetStopsUseCase
import com.ozyuce.maps.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StopsViewModel @Inject constructor(
    private val getStopsUseCase: GetStopsUseCase,
    private val checkStopUseCase: CheckStopUseCase,
    private val getPersonnelUseCase: GetPersonnelUseCase,
    private val addPersonnelUseCase: AddPersonnelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StopsUiState())
    val uiState: StateFlow<StopsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        refresh()
    }

    fun setRoute(routeId: String) {
        _uiState.value = _uiState.value.copy(routeId = routeId)
        refresh()
    }

    fun refresh() {
        loadStops()
        loadPersonnel()
    }

    private fun loadStops() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = getStopsUseCase(_uiState.value.routeId)) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, stops = result.data)
                }
                is AppResult.Error -> {
                    val message = result.exception.message ?: "Duraklar alınamadı"
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
                    _events.emit(UiEvent.ShowSnackbar(message, isError = true))
                }
                is AppResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun loadPersonnel() {
        viewModelScope.launch {
            when (val result = getPersonnelUseCase(_uiState.value.routeId)) {
                is AppResult.Success -> _uiState.value = _uiState.value.copy(personnel = result.data)
                is AppResult.Error -> {
                    val message = result.exception.message ?: "Personel alınamadı"
                    _events.emit(UiEvent.ShowSnackbar(message, isError = true))
                }
                is AppResult.Loading -> {}
            }
        }
    }

    fun markStop(stop: Stop, boarded: Boolean) {
        viewModelScope.launch {
            when (val result = checkStopUseCase(stop.id, boarded)) {
                is AppResult.Success -> {
                    _events.emit(UiEvent.ShowSnackbar("${stop.name} güncellendi"))
                }
                is AppResult.Error -> {
                    val message = result.exception.message ?: "Güncellenemedi"
                    _events.emit(UiEvent.ShowSnackbar(message, isError = true))
                }
                is AppResult.Loading -> {}
            }
        }
    }

    fun addPersonnel(name: String) {
        viewModelScope.launch {
            if (name.isBlank()) return@launch
            val personnel = Personnel(id = name + System.currentTimeMillis(), name = name, active = true)
            when (val result = addPersonnelUseCase(personnel)) {
                is AppResult.Success -> {
                    _events.emit(UiEvent.ShowSnackbar("Personel eklendi"))
                    loadPersonnel()
                }
                is AppResult.Error -> {
                    val message = result.exception.message ?: "Personel eklenemedi"
                    _events.emit(UiEvent.ShowSnackbar(message, isError = true))
                }
                is AppResult.Loading -> {}
            }
        }
    }
}

data class StopsUiState(
    val routeId: String = "default",
    val stops: List<Stop> = emptyList(),
    val personnel: List<Personnel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)