package com.ozyuce.maps.core.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest
) {
    private val locationChannel = Channel<LocationUpdate>(Channel.BUFFERED)
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission") // Caller must handle permissions
    fun startLocationUpdates() {
        if (locationCallback != null) return

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val update = LocationUpdate(
                        location = LatLng(location.latitude, location.longitude),
                        heading = location.bearing,
                        speed = location.speed,
                        accuracy = location.accuracy
                    )
                    locationChannel.trySend(update)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            Timber.d("Location updates started")
        } catch (e: SecurityException) {
            Timber.e(e, "Location permission denied")
        } catch (e: Exception) {
            Timber.e(e, "Error starting location updates")
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            locationCallback = null
            Timber.d("Location updates stopped")
        }
    }

    @SuppressLint("MissingPermission") // Caller must handle permissions
    suspend fun getCurrentLocation(): Location? {
        return try {
            withContext(Dispatchers.IO) {
                Tasks.await(fusedLocationClient.lastLocation)
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Location permission denied")
            null
        } catch (e: Exception) {
            Timber.e(e, "Error getting current location")
            null
        }
    }

    fun getLocationFlow(): Flow<LocationUpdate> = locationChannel.receiveAsFlow()
}

data class LocationUpdate(
    val location: LatLng,
    val heading: Float,
    val speed: Float,
    val accuracy: Float
)
