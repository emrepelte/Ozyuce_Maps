package com.ozyuce.maps.di

import com.ozyuce.maps.core.sample.repository.SampleMapRepositoryImpl
import com.ozyuce.maps.core.sample.repository.SampleReportsRepositoryImpl
import com.ozyuce.maps.core.sample.repository.SampleServiceRepositoryImpl
import com.ozyuce.maps.core.sample.repository.SampleStopsRepositoryImpl
import com.ozyuce.maps.feature.auth.data.repository.AuthRepositoryImpl
import com.ozyuce.maps.feature.auth.domain.AuthRepository
import com.ozyuce.maps.feature.map.domain.MapRepository
import com.ozyuce.maps.feature.reports.domain.ReportsRepository
import com.ozyuce.maps.feature.service.domain.ServiceRepository
import com.ozyuce.maps.feature.stops.domain.StopsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
        sampleRepository: SampleServiceRepositoryImpl
    ): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindStopsRepository(
        sampleRepository: SampleStopsRepositoryImpl
    ): StopsRepository

    @Binds
    @Singleton
    abstract fun bindReportsRepository(
        sampleRepository: SampleReportsRepositoryImpl
    ): ReportsRepository

    @Binds
    @Singleton
    abstract fun bindMapRepository(
        sampleRepository: SampleMapRepositoryImpl
    ): MapRepository
}
