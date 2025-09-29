package com.ozyuce.maps.feature.profile

import android.app.Application
import timber.log.Timber
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.feature.profile.data.Language
import com.ozyuce.maps.feature.profile.data.ThemeMode
import com.ozyuce.maps.feature.profile.data.UserPreferences
import com.ozyuce.maps.feature.profile.logout.ProfileLogoutHandler
import com.ozyuce.maps.feature.profile.theme.ThemeManager
import com.ozyuce.maps.core.common.result.Result as AppResult
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
class ProfileViewModel @Inject constructor(
    private val application: Application,
    private val userPreferences: UserPreferences,
    private val themeManager: ThemeManager,
    private val logoutHandler: ProfileLogoutHandler
) : ViewModel() {

    private val biometricManager: BiometricManager by lazy {
        BiometricManager.from(application)
    }

    private val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            userPreferences.preferences.collect { preferences ->
                val available = biometricManager.canAuthenticate(authenticators) ==
                    BiometricManager.BIOMETRIC_SUCCESS
                _uiState.value = _uiState.value.copy(
                    themeMode = preferences.themeMode,
                    language = preferences.language,
                    isBiometricEnabled = preferences.isBiometricEnabled,
                    isBiometricAvailable = available
                )
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.ThemeModeChanged -> updateThemeMode(event.mode)
            is ProfileEvent.LanguageChanged -> updateLanguage(event.language)
            is ProfileEvent.BiometricToggled -> updateBiometricEnabled(event.enabled)
            ProfileEvent.LogoutRequested -> showLogoutDialog()
            ProfileEvent.LogoutConfirmed -> performLogout()
            ProfileEvent.DismissDialog -> dismissDialog()
            ProfileEvent.ShowThemePicker -> showThemePicker()
            ProfileEvent.DismissThemePicker -> dismissThemePicker()
            ProfileEvent.ShowLanguagePicker -> showLanguagePicker()
            ProfileEvent.DismissLanguagePicker -> dismissLanguagePicker()
        }
    }

    private fun showThemePicker() {
        _uiState.value = _uiState.value.copy(showThemePicker = true)
    }

    private fun dismissThemePicker() {
        _uiState.value = _uiState.value.copy(showThemePicker = false)
    }

    private fun showLanguagePicker() {
        _uiState.value = _uiState.value.copy(showLanguagePicker = true)
    }

    private fun dismissLanguagePicker() {
        _uiState.value = _uiState.value.copy(showLanguagePicker = false)
    }

    private fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                userPreferences.updateThemeMode(mode)
                themeManager.applyThemeMode(mode)
                Timber.d("Tema modu güncellendi ve uygulandı: $mode")
            } catch (e: Exception) {
                Timber.e(e, "Tema modu güncellenirken hata oluştu")
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar("Tema değiştirilirken bir hata oluştu"))
            }
        }
    }

    private fun updateLanguage(language: Language) {
        viewModelScope.launch {
            try {
                userPreferences.updateLanguage(language)
                themeManager.applyLanguage(language)
                Timber.d("Dil güncellendi ve uygulandı: $language")
            } catch (e: Exception) {
                Timber.e(e, "Dil güncellenirken hata oluştu")
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar("Dil değiştirilirken bir hata oluştu"))
            }
        }
    }

    private fun updateBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                val canAuthenticate = biometricManager.canAuthenticate(authenticators) ==
                    BiometricManager.BIOMETRIC_SUCCESS
                if (enabled && !canAuthenticate) {
                    _uiEvent.emit(ProfileUiEvent.ShowSnackbar("Bu cihazda biyometrik kimlik doğrulama kullanılamıyor"))
                    return@launch
                }

                userPreferences.updateBiometricEnabled(enabled)
                Timber.d("Biyometrik kilit durumu güncellendi: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Biyometrik kilit durumu güncellenirken hata oluştu")
                _uiEvent.emit(ProfileUiEvent.ShowSnackbar("Biyometrik kilit durumu değiştirilirken bir hata oluştu"))
            }
        }
    }

    private fun showLogoutDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = true)
    }

    private fun dismissDialog() {
        _uiState.value = _uiState.value.copy(showLogoutDialog = false)
    }

    private fun performLogout() {
        viewModelScope.launch {
            dismissDialog()
            when (val result = logoutHandler.logout()) {
                is AppResult.Success -> {
                    Timber.i("Kullanıcı oturumu kapatıldı")
                    _uiEvent.emit(ProfileUiEvent.LogoutSuccess)
                }
                is AppResult.Error -> {
                    Timber.e(result.exception, "Çıkış işlemi başarısız")
                    _uiEvent.emit(ProfileUiEvent.ShowSnackbar("Çıkış yapılırken bir hata oluştu"))
                }
                AppResult.Loading -> Unit
            }
        }
    }
}

data class ProfileUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: Language = Language.TR,
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showThemePicker: Boolean = false,
    val showLanguagePicker: Boolean = false
)

sealed interface ProfileEvent {
    data class ThemeModeChanged(val mode: ThemeMode) : ProfileEvent
    data class LanguageChanged(val language: Language) : ProfileEvent
    data class BiometricToggled(val enabled: Boolean) : ProfileEvent
    data object LogoutRequested : ProfileEvent
    data object LogoutConfirmed : ProfileEvent
    data object DismissDialog : ProfileEvent
    data object ShowThemePicker : ProfileEvent
    data object DismissThemePicker : ProfileEvent
    data object ShowLanguagePicker : ProfileEvent
    data object DismissLanguagePicker : ProfileEvent
}


sealed interface ProfileUiEvent {
    data class ShowSnackbar(val message: String) : ProfileUiEvent
    data object LogoutSuccess : ProfileUiEvent
}

