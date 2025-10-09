package com.ozyuce.maps.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.auth.domain.LoginUseCase
import com.ozyuce.maps.feature.auth.domain.RegisterUseCase
import com.ozyuce.maps.feature.auth.domain.AuthResult
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
 * Auth ViewModel - Login ve Register i?lemlerini y?netir
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = loginUseCase(email, password)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _uiEvent.emit(UiEvent.Navigate("dashboard"))
                    _uiEvent.emit(UiEvent.ShowSnackbar("Giri? ba?ar?l?! Ho? geldiniz ${result.data.user.name}"))
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, 
                        error = result.exception.message ?: "Giri? ba?ar?s?z"
                    )
                    _uiEvent.emit(UiEvent.ShowSnackbar(_uiState.value.error ?: "Giri? ba?ar?s?z", isError = true))
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun register(email: String, password: String, name: String, role: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = registerUseCase(email, password, name, role)) {
                is OzyuceResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _uiEvent.emit(UiEvent.Navigate("dashboard"))
                    _uiEvent.emit(UiEvent.ShowSnackbar("Kay?t ba?ar?l?! Ho? geldiniz ${result.data.user.name}"))
                }
                is OzyuceResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, 
                        error = result.exception.message ?: "Kay?t ba?ar?s?z"
                    )
                    _uiEvent.emit(UiEvent.ShowSnackbar(_uiState.value.error ?: "Kay?t ba?ar?s?z", isError = true))
                }
                is OzyuceResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Auth UI State
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
