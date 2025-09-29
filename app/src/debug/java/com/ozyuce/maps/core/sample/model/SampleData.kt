package com.ozyuce.maps.core.sample.model

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

data class SampleRoute(
    val id: String = "route_1",
    val name: String = "Merkez Hattı",
    val stops: List<SampleStop>,
    val polyline: List<LatLng>
)

data class SampleStop(
    val id: String,
    val name: String,
    val location: LatLng,
    val scheduledTime: LocalTime,
    val personnel: List<SamplePerson>
)

data class SamplePerson(
    val id: String,
    val name: String,
    val stopId: String,
    val isBoarded: Boolean = false
)

data class SampleServiceSession(
    val id: String,
    val routeId: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val driverId: String
)

object SampleLocations {
    private val CENTER = LatLng(41.0082, 28.9784) // Taksim

    fun generateStopLocations(count: Int, random: Random): List<LatLng> {
        val locations = mutableListOf<LatLng>()
        repeat(count) {
            val lat = CENTER.latitude + (random.nextDouble() - 0.5) * 0.05
            val lng = CENTER.longitude + (random.nextDouble() - 0.5) * 0.05
            locations.add(LatLng(lat, lng))
        }
        return locations
    }

    fun generatePolyline(stops: List<LatLng>): List<LatLng> {
        if (stops.isEmpty()) return emptyList()
        val points = mutableListOf<LatLng>()
        for (i in 0 until stops.size - 1) {
            val start = stops[i]
            val end = stops[i + 1]
            points.add(start)
            repeat(3) { step ->
                val ratio = (step + 1) / 4.0
                val lat = start.latitude + (end.latitude - start.latitude) * ratio
                val lng = start.longitude + (end.longitude - start.longitude) * ratio
                points.add(LatLng(lat, lng))
            }
        }
        points.add(stops.last())
        return points
    }

    fun calculateDistanceKm(points: List<LatLng>): Double {
        if (points.size < 2) return 0.0
        return points.windowed(2).sumOf { (start, end) ->
            val latDiff = abs(end.latitude - start.latitude)
            val lngDiff = abs(end.longitude - start.longitude)
            // Rough distance on small deltas (~111km per degree)
            val distanceDeg = sqrt(latDiff.pow(2) + lngDiff.pow(2))
            distanceDeg * 111.0
        }
    }
}
