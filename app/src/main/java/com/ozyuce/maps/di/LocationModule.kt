package com.ozyuce.maps.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
  @Provides
  @Singleton
  fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(context)

  @Provides
  @Singleton
  fun provideLocationRequest(): LocationRequest =
    LocationRequest.Builder(2000L)
      .setMinUpdateIntervalMillis(1000L)
      .setMinUpdateDistanceMeters(3f)
      .build()
}
