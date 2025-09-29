package com.ozyuce.maps.feature.profile.theme

import timber.log.Timber
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.appcompat.app.AppCompatDelegate
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.ozyuce.maps.feature.profile.data.Language
import com.ozyuce.maps.feature.profile.data.ThemeMode
import com.ozyuce.maps.feature.profile.data.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) {
    val themeMode: Flow<ThemeMode> = userPreferences.preferences.map { it.themeMode }
    val language: Flow<Language> = userPreferences.preferences.map { it.language }

    /**
     * Tema modunu uygular
     */
    fun applyThemeMode(themeMode: ThemeMode) {
        val nightMode = when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        
        AppCompatDelegate.setDefaultNightMode(nightMode)
        Timber.d("Tema modu uygulandı: $themeMode")
    }

    /**
     * Dil ayarını uygular
     */
    fun applyLanguage(language: Language) {
        val locale = when (language) {
            Language.TR -> Locale("tr", "TR")
            Language.EN -> Locale("en", "US")
        }
        
        val localeList = LocaleListCompat.create(locale)
        AppCompatDelegate.setApplicationLocales(localeList)
        Timber.d("Dil uygulandı: $language")
    }
}

/**
 * Composable içinde tema ayarlarını uygulayan yardımcı fonksiyon
 */
@Composable
fun ApplyTheme(
    themeManager: ThemeManager,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeMode by themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val language by themeManager.language.collectAsState(initial = Language.TR)
    
    // Tema değişikliğini uygula
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    
    // Tema ve dil değişikliklerini uygula
    DisposableEffect(themeMode, language) {
        themeManager.applyThemeMode(themeMode)
        themeManager.applyLanguage(language)
        
        onDispose { }
    }
    
    // İçeriği göster
    content()
}
