package com.ozyuce.maps.feature.service.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.usecase.EndServiceUseCase
import com.ozyuce.maps.domain.usecase.StartServiceUseCase
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
class ServiceViewModel @Inject constructor(
    private val startServiceUseCase: StartServiceUseCase,
    private val endServiceUseCase: EndServiceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun startService(routeId: String) {
        submit { startServiceUseCase(routeId) }
    }

    fun endService() {
        submit { endServiceUseCase() }
    }

    private fun submit(action: suspend () -> AppResult<Unit>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = action()) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRunning = !_uiState.value.isRunning
                    )
                    _events.emit(UiEvent.ShowSnackbar("İşlem tamamlandı"))
                }
                is AppResult.Error -> {
                    val message = result.exception.message ?: "İşlem tamamlanamadı"
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
                    _events.emit(UiEvent.ShowSnackbar(message, isError = true))
                }
                is AppResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun setRoute(routeId: String) {
        _uiState.value = _uiState.value.copy(selectedRouteId = routeId)
    }
}

data class ServiceUiState(
    val selectedRouteId: String = "",
    val isRunning: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)