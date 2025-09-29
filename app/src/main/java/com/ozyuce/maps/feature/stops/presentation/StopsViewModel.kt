package com.ozyuce.maps.feature.stops.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.stops.domain.AddPersonnelUseCase
import com.ozyuce.maps.feature.stops.domain.CheckPersonnelUseCase
import com.ozyuce.maps.feature.stops.domain.GetPersonnelUseCase
import com.ozyuce.maps.feature.stops.domain.GetStopsUseCase
import com.ozyuce.maps.feature.stops.domain.StopsRepository
import com.ozyuce.maps.feature.stops.domain.model.Stop
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import com.ozyuce.maps.feature.stops.domain.model.StopStatus
import com.ozyuce.maps.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stops ViewModel - Durak kontrol? ve personel y?netimi
 */
@HiltViewModel
class StopsViewModel @Inject constructor(
    private val getStopsUseCase: GetStopsUseCase,
    private val getPersonnelUseCase: GetPersonnelUseCase,
    private val checkPersonnelUseCase: CheckPersonnelUseCase,
    private val addPersonnelUseCase: AddPersonnelUseCase,
    private val stopsRepository: StopsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StopsUiState())
    val uiState: StateFlow<StopsUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        // Demo i?in route_1 kullan?yoruz
        loadInitialData("route_1")
    }

    private fun loadInitialData(routeId: String) {
        loadStops(routeId)
        loadAllPersonnelForRoute(routeId)
        observeStopsFlow(routeId)
    }

    private fun loadStops(routeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = getStopsUseCase(routeId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stops = result.data,
                        currentRouteId = routeId
                    )
                    
                    // ?lk dura?? se?
                    if (result.data.isNotEmpty() && _uiState.value.selectedStop == null) {
                        selectStop(result.data.first())
                    }
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Duraklar y?klenemedi: ${result.exception.message}"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun loadAllPersonnelForRoute(routeId: String) {
        viewModelScope.launch {
            when (val result = getPersonnelUseCase.getAllForRoute(routeId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        allPersonnel = result.data
                    )
                }
                is Result.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Personel bilgileri y?klenemedi", isError = true))
                }
                is Result.Loading -> { /* Handled by stops loading */ }
            }
        }
    }

    private fun observeStopsFlow(routeId: String) {
        viewModelScope.launch {
            stopsRepository.getStopsFlow(routeId).collect { stops ->
                _uiState.value = _uiState.value.copy(stops = stops)
            }
        }
    }

    fun selectStop(stop: Stop) {
        _uiState.value = _uiState.value.copy(selectedStop = stop)
        loadPersonnelForStop(stop.id)
    }

    private fun loadPersonnelForStop(stopId: String) {
        viewModelScope.launch {
            when (val result = getPersonnelUseCase(stopId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        selectedStopPersonnel = result.data
                    )
                }
                is Result.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Personel listesi y?klenemedi", isError = true))
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }

    fun checkPersonnel(personnelId: String, isChecked: Boolean, notes: String? = null) {
        val selectedStop = _uiState.value.selectedStop ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = checkPersonnelUseCase(
                personnelId = personnelId,
                stopId = selectedStop.id,
                isChecked = isChecked,
                checkedBy = "demo_driver_id", // TODO: Ger?ek s?r?c? ID'si
                notes = notes
            )) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    
                    // Personel listesini g?ncelle
                    updatePersonnelInList(result.data)
                    
                    val action = if (isChecked) "i?aretlendi" else "i?aretlemesi kald?r?ld?"
                    _uiEvent.emit(UiEvent.ShowSnackbar("${result.data.fullName} $action"))
                    
                    // Durak personelini yeniden y?kle
                    loadPersonnelForStop(selectedStop.id)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _uiEvent.emit(UiEvent.ShowSnackbar(
                        result.exception.message ?: "Personel i?aretlenirken hata olu?tu", 
                        isError = true
                    ))
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun addPersonnel(name: String, surname: String, phoneNumber: String?) {
        val selectedStop = _uiState.value.selectedStop ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = addPersonnelUseCase(
                name = name,
                surname = surname,
                stopId = selectedStop.id,
                phoneNumber = phoneNumber
            )) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    
                    _uiEvent.emit(UiEvent.ShowSnackbar("${result.data.fullName} başarıyla eklendi"))
                    
                    // Personel listelerini yeniden y?kle
                    loadPersonnelForStop(selectedStop.id)
                    loadAllPersonnelForRoute(_uiState.value.currentRouteId)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _uiEvent.emit(UiEvent.ShowSnackbar(
                        result.exception.message ?: "Personel eklenirken hata olu?tu", 
                        isError = true
                    ))
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun completeStop() {
        val selectedStop = _uiState.value.selectedStop ?: return
        
        viewModelScope.launch {
            when (val result = stopsRepository.completeStop(selectedStop.id)) {
                is Result.Success -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("${selectedStop.name} tamamland?"))
                    
                    // Dura?? g?ncelle
                    updateStopInList(result.data)
                }
                is Result.Error -> {
                    _uiEvent.emit(UiEvent.ShowSnackbar("Durak tamamlan?rken hata olu?tu", isError = true))
                }
                is Result.Loading -> { /* Loading state already handled */ }
            }
        }
    }

    private fun updatePersonnelInList(updatedPersonnel: Personnel) {
        val current = _uiState.value.selectedStopPersonnel.toMutableList()
        val index = current.indexOfFirst { it.id == updatedPersonnel.id }
        if (index != -1) {
            current[index] = updatedPersonnel
            _uiState.value = _uiState.value.copy(selectedStopPersonnel = current)
        }
    }

    private fun updateStopInList(updatedStop: Stop) {
        val current = _uiState.value.stops.toMutableList()
        val index = current.indexOfFirst { it.id == updatedStop.id }
        if (index != -1) {
            current[index] = updatedStop
            _uiState.value = _uiState.value.copy(
                stops = current,
                selectedStop = if (_uiState.value.selectedStop?.id == updatedStop.id) updatedStop else _uiState.value.selectedStop
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Stops UI State
 */
data class StopsUiState(
    val isLoading: Boolean = false,
    val currentRouteId: String = "route_1",
    val stops: List<Stop> = emptyList(),
    val selectedStop: Stop? = null,
    val selectedStopPersonnel: List<Personnel> = emptyList(),
    val allPersonnel: List<Personnel> = emptyList(),
    val error: String? = null
)
