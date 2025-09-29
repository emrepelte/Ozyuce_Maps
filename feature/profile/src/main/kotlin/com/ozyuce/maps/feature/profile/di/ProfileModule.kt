package com.ozyuce.maps.feature.profile.di

import android.content.Context
import com.ozyuce.maps.feature.profile.biometric.BiometricHelper
import com.ozyuce.maps.feature.profile.data.UserPreferences
import com.ozyuce.maps.feature.profile.theme.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context,
        userPreferences: UserPreferences
    ): ThemeManager {
        return ThemeManager(context, userPreferences)
    }

    @Provides
    @Singleton
    fun provideBiometricHelper(): BiometricHelper = BiometricHelper()
}
