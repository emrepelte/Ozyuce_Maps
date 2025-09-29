package com.ozyuce.maps.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.feature.auth.domain.AuthRepository
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
 * Dashboard ViewModel - Ana ekran ve men? y?netimi
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
    
    init {
        loadUserInfo()
    }
    
    private fun loadUserInfo() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            val userRole = authRepository.getCurrentUserRole()
            
            _uiState.value = _uiState.value.copy(
                userId = userId ?: "unknown",
                userRole = userRole ?: "customer",
                isDriver = userRole == "driver"
            )
        }
    }
    
    fun onMenuItemClicked(menuItem: DashboardMenuItem) {
        viewModelScope.launch {
            when (menuItem) {
                DashboardMenuItem.SERVICE -> _uiEvent.emit(UiEvent.Navigate("service"))
                DashboardMenuItem.STOPS -> _uiEvent.emit(UiEvent.Navigate("stops"))
                DashboardMenuItem.MAP -> _uiEvent.emit(UiEvent.Navigate("map"))
                DashboardMenuItem.REPORTS -> _uiEvent.emit(UiEvent.Navigate("reports"))
                DashboardMenuItem.LOGOUT -> logout()
            }
        }
    }
    
    private fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // AuthRepository'den logout ?a?r?s?
            authRepository.logout()
            
            // Login ekran?na y?nlendir
            _uiEvent.emit(UiEvent.Navigate("login"))
            _uiEvent.emit(UiEvent.ShowSnackbar("??k?? yap?ld?"))
        }
    }
}

/**
 * Dashboard UI State
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val userId: String = "",
    val userRole: String = "customer",
    val isDriver: Boolean = false,
    val error: String? = null
)

/**
 * Dashboard men? ??eleri
 */
enum class DashboardMenuItem {
    SERVICE,    // Servis Y?netimi
    STOPS,      // Durak Kontrol?  
    MAP,        // Harita
    REPORTS,    // Raporlar
    LOGOUT      // ??k??
}
