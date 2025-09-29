package com.ozyuce.maps.di

import com.ozyuce.maps.feature.auth.domain.AuthRepository
import com.ozyuce.maps.feature.profile.domain.LogoutUseCase
import com.ozyuce.maps.feature.stops.domain.StopsRepository
import com.ozyuce.maps.feature.stops.domain.GetStopsUseCase
import com.ozyuce.maps.feature.stops.domain.GetPersonnelUseCase
import com.ozyuce.maps.feature.stops.domain.CheckPersonnelUseCase
import com.ozyuce.maps.feature.stops.domain.AddPersonnelUseCase
import com.ozyuce.maps.feature.reports.domain.ReportsRepository
import com.ozyuce.maps.feature.reports.domain.GetDailyReportUseCase
import com.ozyuce.maps.feature.reports.domain.GetWeeklyReportUseCase
import com.ozyuce.maps.feature.reports.domain.GetReportChartsUseCase
import com.ozyuce.maps.feature.map.domain.GetRouteDetailsUseCase
import com.ozyuce.maps.feature.map.domain.TrackVehicleLocationUseCase
import com.ozyuce.maps.feature.map.domain.CalculateEtaUseCase
import com.ozyuce.maps.feature.map.domain.MapRepository
import com.ozyuce.maps.core.common.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Use Case'ler i?in Hilt mod?l?
 * Use case'ler constructor injection ile otomatik sa?lan?r
 * ?zel konfig?rasyon gereken durumlarda @Provides kullan?labilir
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    // Use case'ler otomatik olarak inject edilir
    // ?zel durumlarda manuel @Provides eklenebilir
    
    @Provides
    @Singleton
    fun provideGetStopsUseCase(
        stopsRepository: StopsRepository,
        dispatcherProvider: DispatcherProvider
    ): GetStopsUseCase {
        return GetStopsUseCase(stopsRepository, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideGetPersonnelUseCase(
        stopsRepository: StopsRepository,
        dispatcherProvider: DispatcherProvider
    ): GetPersonnelUseCase {
        return GetPersonnelUseCase(stopsRepository, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideCheckPersonnelUseCase(
        stopsRepository: StopsRepository,
        dispatcherProvider: DispatcherProvider
    ): CheckPersonnelUseCase {
        return CheckPersonnelUseCase(stopsRepository, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideAddPersonnelUseCase(
        stopsRepository: StopsRepository,
        dispatcherProvider: DispatcherProvider
    ): AddPersonnelUseCase {
        return AddPersonnelUseCase(stopsRepository, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository
    ): LogoutUseCase {
        return LogoutUseCase {
            authRepository.logout()
        }
    }

    @Provides
    @Singleton
    fun provideGetDailyReportUseCase(
        reportsRepository: ReportsRepository,
        dispatcherProvider: DispatcherProvider
    ): GetDailyReportUseCase {
        return GetDailyReportUseCase(reportsRepository, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideGetWeeklyReportUseCase(
        reportsRepository: ReportsRepository,
        dispatcherProvider: DispatcherProvider
    ): GetWeeklyReportUseCase {
        return GetWeeklyReportUseCase(reportsRepository, dispatcherProvider)
    }

        @Provides
        @Singleton
        fun provideGetReportChartsUseCase(
            reportsRepository: ReportsRepository,
            dispatcherProvider: DispatcherProvider
        ): GetReportChartsUseCase {
            return GetReportChartsUseCase(reportsRepository, dispatcherProvider)
        }

        @Provides
        @Singleton
        fun provideGetRouteDetailsUseCase(
            mapRepository: MapRepository,
            dispatcherProvider: DispatcherProvider
        ): GetRouteDetailsUseCase {
            return GetRouteDetailsUseCase(mapRepository, dispatcherProvider)
        }

        @Provides
        @Singleton
        fun provideTrackVehicleLocationUseCase(
            mapRepository: MapRepository,
            dispatcherProvider: DispatcherProvider
        ): TrackVehicleLocationUseCase {
            return TrackVehicleLocationUseCase(mapRepository, dispatcherProvider)
        }

        @Provides
        @Singleton
        fun provideCalculateEtaUseCase(
            mapRepository: MapRepository,
            dispatcherProvider: DispatcherProvider
        ): CalculateEtaUseCase {
            return CalculateEtaUseCase(mapRepository, dispatcherProvider)
        }
    
    /*
    @Provides
    fun provideAuthUseCases(
        loginUseCase: LoginUseCase,
        registerUseCase: RegisterUseCase
    ): AuthUseCases {
        return AuthUseCases(
            login = loginUseCase,
            register = registerUseCase
        )
    }
    */
}
