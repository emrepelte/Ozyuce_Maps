package com.ozyuce.maps.di

import com.ozyuce.maps.data.repository.DefaultAuthRepository
import com.ozyuce.maps.data.repository.DefaultNotificationsRepository
import com.ozyuce.maps.data.repository.DefaultPersonnelRepository
import com.ozyuce.maps.data.repository.DefaultReportsRepository
import com.ozyuce.maps.data.repository.DefaultServiceRepository
import com.ozyuce.maps.data.repository.DefaultStopsRepository
import com.ozyuce.maps.domain.repository.AuthRepository
import com.ozyuce.maps.domain.repository.NotificationsRepository
import com.ozyuce.maps.domain.repository.PersonnelRepository
import com.ozyuce.maps.domain.repository.ReportsRepository
import com.ozyuce.maps.domain.repository.ServiceRepository
import com.ozyuce.maps.domain.repository.StopsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: DefaultAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(impl: DefaultServiceRepository): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindStopsRepository(impl: DefaultStopsRepository): StopsRepository

    @Binds
    @Singleton
    abstract fun bindPersonnelRepository(impl: DefaultPersonnelRepository): PersonnelRepository

    @Binds
    @Singleton
    abstract fun bindReportsRepository(impl: DefaultReportsRepository): ReportsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(impl: DefaultNotificationsRepository): NotificationsRepository
}