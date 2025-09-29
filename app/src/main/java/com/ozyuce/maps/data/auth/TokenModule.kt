package com.ozyuce.maps.data.auth

import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenModule {

    @Binds
    @Singleton
    abstract fun bindAuthTokenProvider(
        impl: DataStoreAuthTokenProvider
    ): AuthTokenProvider
}