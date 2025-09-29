package com.ozyuce.maps.core.sample

import com.ozyuce.maps.core.sample.model.SampleLocations
import com.ozyuce.maps.core.sample.model.SamplePerson
import com.ozyuce.maps.core.sample.model.SampleRoute
import com.ozyuce.maps.core.sample.model.SampleServiceSession
import com.ozyuce.maps.core.sample.model.SampleStop
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SampleDataProvider @Inject constructor() {

    private val random = Random(42)

    private val stopNames = listOf(
        "Taksim Meydani",
        "Besiktas Iskele",
        "Mecidiyekoy Metro",
        "Sisli Merkez",
        "Levent Metro",
        "Maslak Plaza",
        "Sariyer Merkez",
        "Kadikoy Iskele"
    )

    private val personNames = listOf(
        "Ahmet Yilmaz",
        "Mehmet Demir",
        "Ayse Kaya",
        "Fatma Celik",
        "Ali Ozturk",
        "Zeynep Aydin",
        "Mustafa Sahin",
        "Can Yildiz",
        "Elif Arslan",
        "Deniz Koc",
        "Burak Ozdemir",
        "Selin Aktas"
    )

    private val cachedRoute: SampleRoute by lazy { createSampleRoute() }

    fun sampleRoute(): SampleRoute = cachedRoute

    fun createRouteVariants(): List<SampleRoute> {
        val base = cachedRoute
        return listOf(
            base,
            base.copy(id = "route_2", name = "Sehir Ici Hatti"),
            base.copy(id = "route_3", name = "Sanayi Hatti")
        )
    }

    fun createServiceSession(routeId: String = cachedRoute.id): SampleServiceSession {
        val start = LocalDateTime.now().withHour(8).withMinute(0)
        return SampleServiceSession(
            id = "session_",
            routeId = routeId,
            startTime = start,
            driverId = "driver_demo"
        )
    }

    private fun createSampleRoute(): SampleRoute {
        val stopCount = random.nextInt(6, 9)
        val locations = SampleLocations.generateStopLocations(stopCount, random)

        val stops = locations.mapIndexed { index, location ->
            val scheduledTime = LocalTime.of(8, 0).plusMinutes((index * 12).toLong())
            val personnelCount = random.nextInt(2, 5)
            val personnel = generatePersonnel(personnelCount, "stop_")

            SampleStop(
                id = "stop_",
                name = stopNames.getOrElse(index) { "Durak " },
                location = location,
                scheduledTime = scheduledTime,
                personnel = personnel
            )
        }

        val polyline = SampleLocations.generatePolyline(locations)

        return SampleRoute(
            id = "route_1",
            name = "Merkez Hatti",
            stops = stops,
            polyline = polyline
        )
    }

    private fun generatePersonnel(count: Int, stopId: String): List<SamplePerson> {
        return personNames.shuffled(random).take(count).mapIndexed { index, name ->
            SamplePerson(
                id = "person__",
                name = name,
                stopId = stopId,
                isBoarded = false
            )
        }
    }
}
