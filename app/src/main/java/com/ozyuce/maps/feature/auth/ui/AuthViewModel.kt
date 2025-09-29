package com.ozyuce.maps.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.usecase.LoginUseCase
import com.ozyuce.maps.domain.usecase.LogoutUseCase
import com.ozyuce.maps.domain.usecase.RegisterUseCase
import com.ozyuce.maps.navigation.Dest
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
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun login() {
        submit { loginUseCase(_uiState.value.email.trim(), _uiState.value.password) }
    }

    fun register() {
        submit { registerUseCase(_uiState.value.email.trim(), _uiState.value.password) }
    }

    fun logout() {
        submit { logoutUseCase() }
    }

    private fun submit(block: suspend () -> AppResult<Unit>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = block()) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.emit(UiEvent.Navigate(Dest.Dashboard.route))
                    _events.emit(UiEvent.ShowSnackbar("İşlem başarılı"))
                }
                is AppResult.Error -> {
                    val message = result.exception.message ?: "İşlem başarısız"
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
                    _events.emit(UiEvent.ShowSnackbar(message, isError = true))
                }
                is AppResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)