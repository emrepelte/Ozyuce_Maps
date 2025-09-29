package com.ozyuce.maps.di

import com.ozyuce.maps.feature.auth.data.repository.AuthRepositoryImpl
import com.ozyuce.maps.feature.auth.domain.AuthRepository
import com.ozyuce.maps.feature.map.data.repository.MapRepositoryImpl
import com.ozyuce.maps.feature.map.domain.MapRepository
import com.ozyuce.maps.feature.reports.data.repository.ReportsRepositoryImpl
import com.ozyuce.maps.feature.reports.domain.ReportsRepository
import com.ozyuce.maps.feature.service.data.repository.ServiceRepositoryImpl
import com.ozyuce.maps.feature.service.domain.ServiceRepository
import com.ozyuce.maps.feature.stops.data.repository.StopsRepositoryImpl
import com.ozyuce.maps.feature.stops.domain.StopsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository implementasyonlar? i?in Hilt mod?l?
 * Interface -> Implementation binding'leri burada yap?l?r
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(
        serviceRepositoryImpl: ServiceRepositoryImpl
    ): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindStopsRepository(
        stopsRepositoryImpl: StopsRepositoryImpl
    ): StopsRepository

    @Binds
    @Singleton
    abstract fun bindReportsRepository(
        reportsRepositoryImpl: ReportsRepositoryImpl
    ): ReportsRepository

    @Binds
    @Singleton
    abstract fun bindMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ): MapRepository

    // Di?er repository binding'leri ilerleyen ad?mlarda eklenecek
}
