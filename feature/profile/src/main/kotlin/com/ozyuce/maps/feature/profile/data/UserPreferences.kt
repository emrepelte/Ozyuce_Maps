package com.ozyuce.maps.feature.profile.data

import timber.log.Timber
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val preferences: Flow<UserPreferencesData> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "DataStore okuma hatası")
                emit(emptyPreferences())
            } else {
                Timber.e(exception, "DataStore bilinmeyen hata")
                throw exception
            }
        }
        .map { preferences ->
            val themeModeName = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val languageName = preferences[PreferencesKeys.LANGUAGE] ?: Language.TR.name
            val isBiometricEnabled = preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false
            
            try {
                UserPreferencesData(
                    themeMode = ThemeMode.valueOf(themeModeName),
                    language = Language.valueOf(languageName),
                    isBiometricEnabled = isBiometricEnabled
                )
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Geçersiz enum değeri: theme=$themeModeName, language=$languageName")
                UserPreferencesData() // Varsayılan değerlerle dön
            }
        }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.THEME_MODE] = themeMode.name
            }
            Timber.d("Tema modu güncellendi: $themeMode")
        } catch (e: Exception) {
            Timber.e(e, "Tema modu güncellenirken hata oluştu")
        }
    }

    suspend fun updateLanguage(language: Language) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.LANGUAGE] = language.name
            }
            Timber.d("Dil güncellendi: $language")
        } catch (e: Exception) {
            Timber.e(e, "Dil güncellenirken hata oluştu")
        }
    }

    suspend fun updateBiometricEnabled(enabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.BIOMETRIC_ENABLED] = enabled
            }
            Timber.d("Biyometrik kilit güncellendi: $enabled")
        } catch (e: Exception) {
            Timber.e(e, "Biyometrik kilit güncellenirken hata oluştu")
        }
    }

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }
}

data class UserPreferencesData(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: Language = Language.TR,
    val isBiometricEnabled: Boolean = false
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class Language {
    TR, EN
}
