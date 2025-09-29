package com.ozyuce.maps.di

import com.ozyuce.maps.core.common.DefaultDispatcherProvider
import com.ozyuce.maps.core.common.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Ortak ba??ml?l?klar i?in Hilt mod?l?
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {
    
    @Binds
    abstract fun bindDispatcherProvider(
        defaultDispatcherProvider: DefaultDispatcherProvider
    ): DispatcherProvider
}
