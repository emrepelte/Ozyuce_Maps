package com.ozyuce.maps.feature.service.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.service.domain.EndServiceUseCase
import com.ozyuce.maps.feature.service.domain.ServiceRepository
import com.ozyuce.maps.feature.service.domain.StartServiceUseCase
import com.ozyuce.maps.feature.service.domain.model.Route
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import com.ozyuce.maps.feature.service.domain.model.ServiceStatus
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
 * Service ViewModel - Servis ba?latma/bitirme y?netimi
 */
@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val startServiceUseCase: StartServiceUseCase,
    private val endServiceUseCase: EndServiceUseCase,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        loadInitialData()
        observeCurrentSession()
    }

    private fun loadInitialData() {
        loadAvailableRoutes()
        loadCurrentSession()
    }

    private fun observeCurrentSession() {
        viewModelScope.launch {
            serviceRepository.getCurrentSessionFlow().collect { session ->
                _uiState.value = _uiState.value.copy(
                    currentSession = session,
                    serviceStatus = when {
                        session?.isActive == true -> ServiceStatus.ACTIVE
                        else -> ServiceStatus.IDLE
                    }
                )
            }
        }
    }

    private fun loadAvailableRoutes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = serviceRepository.getAvailableRoutes()) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        availableRoutes = result.data
                    )
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Rotalar y?klenemedi: ${result.exception.message}"
                    )
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun loadCurrentSession() {
        viewModelScope.launch {
            when (val result = serviceRepository.getCurrentSession()) {
                is OzyuceResult.Success -> {
                    val session = result.data
                    _uiState.value = _uiState.value.copy(
                        currentSession = session,
                        serviceStatus = when {
                            session?.isActive == true -> ServiceStatus.ACTIVE
                            else -> ServiceStatus.IDLE
                        }
                    )
                }
                is OzyuceResult.Error -> {
                    // Sessiona ula??lamad?, IDLE durumda kal
                    _uiState.value = _uiState.value.copy(serviceStatus = ServiceStatus.IDLE)
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun startService(routeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = startServiceUseCase(routeId)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentSession = result.data,
                        serviceStatus = ServiceStatus.ACTIVE
                    )
                    _uiEvent.emit(UiEvent.ShowSnackbar("Servis ba?ar?yla ba?lat?ld?!"))
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Servis ba?lat?lamad?"
                    )
                    _uiEvent.emit(UiEvent.ShowSnackbar(_uiState.value.error ?: "Servis ba?lat?lamad?", isError = true))
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun endService() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = endServiceUseCase()) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentSession = result.data,
                        serviceStatus = ServiceStatus.COMPLETED
                    )
                    _uiEvent.emit(UiEvent.ShowSnackbar("Servis ba?ar?yla tamamland?!"))
                    
                    // 2 saniye sonra IDLE durumuna ge?
                    kotlinx.coroutines.delay(2000)
                    _uiState.value = _uiState.value.copy(
                        serviceStatus = ServiceStatus.IDLE,
                        currentSession = null
                    )
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Servis bitirilemedi"
                    )
                    _uiEvent.emit(UiEvent.ShowSnackbar(_uiState.value.error ?: "Servis bitirilemedi", isError = true))
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun selectRoute(route: Route) {
        _uiState.value = _uiState.value.copy(selectedRoute = route)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Service UI State
 */
data class ServiceUiState(
    val isLoading: Boolean = false,
    val serviceStatus: ServiceStatus = ServiceStatus.IDLE,
    val currentSession: ServiceSession? = null,
    val availableRoutes: List<Route> = emptyList(),
    val selectedRoute: Route? = null,
    val error: String? = null
)

