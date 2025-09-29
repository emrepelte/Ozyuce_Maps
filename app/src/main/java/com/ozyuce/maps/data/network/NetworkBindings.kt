package com.ozyuce.maps.data.network

import com.ozyuce.maps.core.network.di.NetworkModule.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkBindings {

    @Provides
    @Singleton
    @AuthInterceptor
    fun provideAuthInterceptor(interceptor: AuthTokenInterceptor): Interceptor = interceptor
}