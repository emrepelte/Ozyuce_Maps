package com.ozyuce.maps.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationClient @Inject constructor(
  private val fusedLocationProviderClient: FusedLocationProviderClient,
  private val locationRequest: LocationRequest
) {
  @SuppressLint("MissingPermission")
  fun stream(): Flow<Location> = callbackFlow {
    val callback = object : LocationCallback() {
      override fun onLocationResult(result: LocationResult) {
        result.lastLocation?.let { trySend(it).isSuccess }
      }
    }
    fusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, null)
    awaitClose { fusedLocationProviderClient.removeLocationUpdates(callback) }
  }
}
