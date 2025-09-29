package com.ozyuce.maps.core.designsystem.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(themeMode = themeMode))
        }
    }

    fun toggleThemeMode() {
        val nextMode = when (_uiState.value.themeMode) {
            ThemeMode.System -> ThemeMode.Dark
            ThemeMode.Dark -> ThemeMode.Light
            ThemeMode.Light -> ThemeMode.System
        }
        setThemeMode(nextMode)
    }

    fun toggleDynamicColor() {
        val current = _uiState.value.useDynamicColor
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(useDynamicColor = !current))
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(useDynamicColor = enabled))
        }
    }
}

data class ThemeUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val useDynamicColor: Boolean = false
)
