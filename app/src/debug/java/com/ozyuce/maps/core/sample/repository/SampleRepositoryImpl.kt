package com.ozyuce.maps.core.sample.repository

import com.google.android.gms.maps.model.LatLng
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.core.sample.SampleDataProvider
import com.ozyuce.maps.core.sample.model.SamplePerson
import com.ozyuce.maps.core.sample.model.SampleRoute
import com.ozyuce.maps.core.sample.model.SampleServiceSession
import com.ozyuce.maps.core.sample.model.SampleStop
import com.ozyuce.maps.feature.map.domain.MapRepository
import com.ozyuce.maps.feature.map.domain.model.RouteEta
import com.ozyuce.maps.feature.map.domain.model.RoutePolyline
import com.ozyuce.maps.feature.map.domain.model.StopMarker
import com.ozyuce.maps.feature.map.domain.model.VehicleLocation
import com.ozyuce.maps.feature.reports.domain.ReportsRepository
import com.ozyuce.maps.feature.reports.domain.model.AttendanceData
import com.ozyuce.maps.feature.reports.domain.model.ChartData
import com.ozyuce.maps.feature.reports.domain.model.ChartEntry
import com.ozyuce.maps.feature.reports.domain.model.ChartType
import com.ozyuce.maps.feature.reports.domain.model.DailyReport
import com.ozyuce.maps.feature.reports.domain.model.ReportFilter
import com.ozyuce.maps.feature.reports.domain.model.ReportSummary
import com.ozyuce.maps.feature.reports.domain.model.TimeAnalysis
import com.ozyuce.maps.feature.reports.domain.model.PerformanceMetrics
import com.ozyuce.maps.feature.reports.domain.model.Trend
import com.ozyuce.maps.feature.reports.domain.model.TrendDirection
import com.ozyuce.maps.feature.reports.domain.model.WeeklyReport
import com.ozyuce.maps.feature.reports.domain.model.WeeklySummary
import com.ozyuce.maps.feature.service.domain.ServiceRepository
import com.ozyuce.maps.feature.service.domain.model.Route
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import com.ozyuce.maps.feature.service.domain.model.ServiceStatus
import com.ozyuce.maps.feature.stops.domain.StopsRepository
import com.ozyuce.maps.feature.stops.domain.model.AddPersonnelRequest
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import com.ozyuce.maps.feature.stops.domain.model.PersonnelCheck
import com.ozyuce.maps.feature.stops.domain.model.Stop
import com.ozyuce.maps.feature.stops.domain.model.StopStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.random.Random

@Singleton
class SampleServiceRepositoryImpl @Inject constructor(
    private val sampleDataProvider: SampleDataProvider
) : ServiceRepository {

    private val routeVariants = sampleDataProvider.createRouteVariants().map { it.toServiceRoute() }
    private val currentSession = MutableStateFlow<ServiceSession?>(null)
    private val history = mutableListOf<ServiceSession>()

    override suspend fun startService(routeId: String): OzyuceResult<ServiceSession> {
        delay(150)
        val route = routeVariants.firstOrNull { it.id == routeId } ?: routeVariants.first()
        val session = sampleDataProvider.createServiceSession(route.id).toDomain()
        currentSession.value = session
        return OzyuceResult.Success(session)
    }

    override suspend fun endService(sessionId: String): OzyuceResult<ServiceSession> {
        delay(150)
        val session = currentSession.value ?: return OzyuceResult.Error(IllegalStateException("Aktif servis bulunamadi"))
        if (session.id != sessionId) {
            return OzyuceResult.Error(IllegalArgumentException("Servis kimligi eslesmedi"))
        }
        val ended = session.copy(
            endTime = Date(),
            isActive = false,
            totalDistance = session.totalStops * 2.4,
            averageSpeed = 32.5,
            fuelConsumption = 5.8,
            stopsCompleted = session.totalStops
        )
        history.add(0, ended)
        currentSession.value = ended
        return OzyuceResult.Success(ended)
    }

    override suspend fun getCurrentSession(): OzyuceResult<ServiceSession?> {
        delay(100)
        return OzyuceResult.Success(currentSession.value)
    }

    override fun getCurrentSessionFlow(): Flow<ServiceSession?> = currentSession.asStateFlow()

    override suspend fun getAvailableRoutes(): OzyuceResult<List<Route>> {
        delay(100)
        return OzyuceResult.Success(routeVariants)
    }

    override suspend fun getServiceHistory(): OzyuceResult<List<ServiceSession>> {
        delay(120)
        return OzyuceResult.Success(history.take(5))
    }

    private fun SampleRoute.toServiceRoute(): Route {
        val distance = samplePolylineDistance(polyline)
        val estimatedDuration = stops.size * 15
        return Route(
            id = id,
            name = name,
            description = "Ornek servis rotasi",
            totalStops = stops.size,
            estimatedDuration = estimatedDuration,
            estimatedDistance = distance
        )
    }

    private fun SampleServiceSession.toDomain(): ServiceSession {
        return ServiceSession(
            id = id,
            routeId = routeId,
            driverId = driverId,
            startTime = startTime.toDate(),
            endTime = endTime?.toDate(),
            isActive = true,
            totalDistance = 0.0,
            averageSpeed = 0.0,
            fuelConsumption = null,
            stopsCompleted = 0,
            totalStops = sampleDataProvider.sampleRoute().stops.size
        )
    }
}

@Singleton
class SampleStopsRepositoryImpl @Inject constructor(
    private val sampleDataProvider: SampleDataProvider
) : StopsRepository {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val route = sampleDataProvider.sampleRoute()

    private val stopsState = MutableStateFlow(route.stops.map { it.toDomain(timeFormatter) })
    private val personnelState = MutableStateFlow(route.stops.associate { stop ->
        stop.id to stop.personnel.map { it.toDomain(stop) }
    })

    override suspend fun getStopsForRoute(routeId: String): OzyuceResult<List<Stop>> {
        delay(120)
        return OzyuceResult.Success(stopsState.value)
    }

    override suspend fun getPersonnelForStop(stopId: String): OzyuceResult<List<Personnel>> {
        delay(80)
        return OzyuceResult.Success(personnelState.value[stopId].orEmpty())
    }

    override suspend fun getAllPersonnelForRoute(routeId: String): OzyuceResult<List<Personnel>> {
        delay(100)
        return OzyuceResult.Success(personnelState.value.values.flatten())
    }

    override suspend fun checkPersonnel(personnelCheck: PersonnelCheck): OzyuceResult<Personnel> {
        delay(60)
        val personnelMap = personnelState.value.toMutableMap()
        val list = personnelMap[personnelCheck.stopId]?.toMutableList() ?: return OzyuceResult.Error(IllegalArgumentException("Personel bulunamadi"))
        val index = list.indexOfFirst { it.id == personnelCheck.personnelId }
        if (index == -1) return OzyuceResult.Error(IllegalArgumentException("Personel bulunamadi"))

        val updated = list[index].copy(
            isChecked = personnelCheck.isChecked,
            checkTime = personnelCheck.checkTime,
            checkedBy = personnelCheck.checkedBy,
            notes = personnelCheck.notes
        )
        list[index] = updated
        personnelMap[personnelCheck.stopId] = list
        personnelState.value = personnelMap
        refreshStop(personnelCheck.stopId)
        return OzyuceResult.Success(updated)
    }

    override suspend fun addPersonnel(request: AddPersonnelRequest): OzyuceResult<Personnel> {
        delay(120)
        val newPersonnel = Personnel(
            id = "person_",
            name = request.name,
            surname = request.surname,
            stopId = request.stopId,
            stopName = stopsState.value.firstOrNull { it.id == request.stopId }?.name ?: "",
            phoneNumber = request.phoneNumber,
            isChecked = false
        )
        val personnelMap = personnelState.value.toMutableMap()
        val list = personnelMap.getOrElse(request.stopId) { emptyList() }.toMutableList()
        list.add(newPersonnel)
        personnelMap[request.stopId] = list
        personnelState.value = personnelMap
        refreshStop(request.stopId)
        return OzyuceResult.Success(newPersonnel)
    }

    override suspend fun completeStop(stopId: String): OzyuceResult<Stop> {
        delay(100)
        val stops = stopsState.value.toMutableList()
        val index = stops.indexOfFirst { it.id == stopId }
        if (index == -1) return OzyuceResult.Error(IllegalArgumentException("Durak bulunamadi"))
        val completed = stops[index].copy(
            isCompleted = true,
            checkedPersonnelCount = personnelState.value[stopId]?.count { it.isChecked } ?: 0
        )
        stops[index] = completed
        stopsState.value = stops
        return OzyuceResult.Success(completed)
    }

    override fun getStopsFlow(routeId: String): Flow<List<Stop>> = stopsState.asStateFlow()

    override fun getPersonnelFlow(stopId: String): Flow<List<Personnel>> {
        return personnelState.asStateFlow().map { it[stopId].orEmpty() }
    }

    internal fun stopsStateFlow(): Flow<List<Stop>> = stopsState.asStateFlow()

    internal fun currentStops(): List<Stop> = stopsState.value

    private fun refreshStop(stopId: String) {
        val stops = stopsState.value.toMutableList()
        val index = stops.indexOfFirst { it.id == stopId }
        if (index == -1) return
        val personnel = personnelState.value[stopId].orEmpty()
        stops[index] = stops[index].copy(
            personnelCount = personnel.size,
            checkedPersonnelCount = personnel.count { it.isChecked }
        )
        stopsState.value = stops
    }

    private fun SampleStop.toDomain(formatter: DateTimeFormatter): Stop {
        val personnel = personnelState.value[id].orEmpty()
        return Stop(
            id = id,
            name = name,
            description = "Ornek durak",
            latitude = location.latitude,
            longitude = location.longitude,
            sequence = id.substringAfter('_').toIntOrNull() ?: 0,
            estimatedArrivalTime = formatter.format(scheduledTime),
            actualArrivalTime = null,
            isCompleted = false,
            personnelCount = personnel.size,
            checkedPersonnelCount = personnel.count { it.isChecked }
        )
    }

    private fun SamplePerson.toDomain(stop: SampleStop): Personnel {
        val parts = name.split(' ')
        val first = parts.firstOrNull() ?: name
        val last = parts.drop(1).joinToString(" ")
        return Personnel(
            id = id,
            name = first,
            surname = last,
            stopId = stop.id,
            stopName = stop.name,
            isChecked = isBoarded,
            fullName = name
        )
    }
}

@Singleton
class SampleMapRepositoryImpl @Inject constructor(
    private val sampleDataProvider: SampleDataProvider,
    private val sampleStopsRepository: SampleStopsRepositoryImpl
) : MapRepository {

    private val vehicleLocation = MutableStateFlow(createInitialVehicleLocation())
    private val route = sampleDataProvider.sampleRoute()

    override suspend fun updateVehicleLocation(
        location: LatLng,
        heading: Float,
        speed: Float
    ): OzyuceResult<VehicleLocation> {
        delay(50)
        val updated = VehicleLocation(
            id = "vehicle_",
            routeId = route.id,
            driverId = "driver_demo",
            location = location,
            heading = heading,
            speed = speed,
            timestamp = Date()
        )
        vehicleLocation.value = updated
        return OzyuceResult.Success(updated)
    }

    override fun getVehicleLocationFlow(): Flow<VehicleLocation?> = vehicleLocation.asStateFlow()

    override suspend fun startLocationUpdates() { /* no-op for sample */ }

    override suspend fun stopLocationUpdates() { /* no-op for sample */ }

    override suspend fun getRoutePolyline(routeId: String): OzyuceResult<RoutePolyline> {
        delay(80)
        val points = route.polyline
        val distance = samplePolylineDistance(points)
        val duration = max(route.stops.size * 12, 30)
        return OzyuceResult.Success(
            RoutePolyline(
                routeId = routeId,
                points = points,
                distance = distance,
                duration = duration,
                trafficDuration = duration + 6
            )
        )
    }

    override suspend fun getStopMarkers(routeId: String): OzyuceResult<List<StopMarker>> {
        delay(60)
        return OzyuceResult.Success(sampleStopsRepository.currentStops().toMarkers())
    }

    override fun getStopMarkersFlow(routeId: String): Flow<List<StopMarker>> {
        return sampleStopsRepository.stopsStateFlow().map { it.toMarkers() }
    }

    override suspend fun calculateEta(origin: LatLng, destination: LatLng): OzyuceResult<RouteEta> {
        delay(40)
        val duration = 20 + Random.nextInt(0, 15)
        return OzyuceResult.Success(
            RouteEta(
                stopId = "eta__",
                estimatedArrival = Date(System.currentTimeMillis() + duration * 60 * 1000L),
                distance = 5.0 + Random.nextDouble(0.0, 4.0),
                duration = duration,
                trafficDuration = duration + 4
            )
        )
    }

    override suspend fun calculateBatchEta(
        origin: LatLng,
        destinations: List<LatLng>
    ): OzyuceResult<List<RouteEta>> {
        delay(60)
        val results = destinations.mapIndexed { index, dest ->
            val baseDuration = 15 + index * 5
            RouteEta(
                stopId = "stop_eta_",
                estimatedArrival = Date(System.currentTimeMillis() + baseDuration * 60 * 1000L),
                distance = 4.0 + index * 2,
                duration = baseDuration,
                trafficDuration = baseDuration + 3
            )
        }
        return OzyuceResult.Success(results)
    }

    override suspend fun connectWebSocket() { /* no-op */ }

    override suspend fun disconnectWebSocket() { /* no-op */ }

    override suspend fun sendLocationUpdate(location: VehicleLocation) {
        vehicleLocation.value = location
    }

    private fun createInitialVehicleLocation(): VehicleLocation {
        val firstStop = sampleStopsRepository.currentStops().firstOrNull()
        val initialLocation = firstStop?.let { LatLng(it.latitude, it.longitude) }
            ?: LatLng(41.0082, 28.9784)
        return VehicleLocation(
            id = "vehicle_initial",
            routeId = sampleDataProvider.sampleRoute().id,
            driverId = "driver_demo",
            location = initialLocation,
            heading = 0f,
            speed = 0f,
            timestamp = Date(),
            isActive = false
        )
    }

    private fun List<Stop>.toMarkers(): List<StopMarker> {
        return mapIndexed { index, stop ->
            StopMarker(
                id = stop.id,
                name = stop.name,
                location = LatLng(stop.latitude, stop.longitude),
                sequence = index + 1,
                scheduledTime = stop.estimatedArrivalTime ?: "08:00",
                isCompleted = stop.isCompleted,
                completedTime = null,
                personnelCount = stop.personnelCount,
                boardedCount = stop.checkedPersonnelCount
            )
        }
    }
}

@Singleton
class SampleReportsRepositoryImpl @Inject constructor(
    private val sampleStopsRepository: SampleStopsRepositoryImpl,
    private val sampleServiceRepository: SampleServiceRepositoryImpl
) : ReportsRepository {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override suspend fun getDailyReport(date: String, routeId: String): OzyuceResult<DailyReport> {
        delay(120)
        val stops = sampleStopsRepository.currentStops()
        val totalPersonnel = stops.sumOf { it.personnelCount }
        val attended = stops.sumOf { it.checkedPersonnelCount }
        val summary = createSummary(totalPersonnel, attended)
        val attendance = AttendanceData(
            attendedCount = attended,
            absentCount = max(totalPersonnel - attended, 0),
            lateCount = max(attended / 6, 1),
            earlyLeaveCount = max(attended / 8, 1)
        )
        val timeAnalysis = TimeAnalysis(
            earliestStart = "08:00",
            latestEnd = "10:15",
            averageServiceDuration = summary.averageDuration,
            onTimePerformance = 92.5f,
            delayMinutes = 4.5
        )
        val performance = PerformanceMetrics(
            punctualityScore = 94f,
            efficiencyScore = 89f,
            customerSatisfaction = 91f,
            fuelEfficiency = 5.6f,
            stopCompleteRate = 96f,
            overallScore = PerformanceMetrics.calculateOverallScore(94f, 89f, 91f, 96f)
        )
        val report = DailyReport(
            date = date,
            routeId = routeId,
            routeName = "Ornek Rota",
            driverName = "Demo Surucu",
            summary = summary,
            attendanceData = attendance,
            timeAnalysis = timeAnalysis,
            performanceMetrics = performance
        )
        return OzyuceResult.Success(report)
    }

    override suspend fun getWeeklyReport(weekStartDate: String, routeId: String): OzyuceResult<WeeklyReport> {
        delay(160)
        val dailyReports = (0 until 7).map { offset ->
            val date = LocalDate.parse(weekStartDate, dateFormatter).plusDays(offset.toLong())
            when (val result = getDailyReport(dateFormatter.format(date), routeId)) {
                is OzyuceResult.Success -> result.data
                is OzyuceResult.Error -> throw result.exception
                OzyuceResult.Loading -> throw IllegalStateException("Unexpected loading state")
            }
        }
        val weekSummary = WeeklySummary(
            totalServices = dailyReports.sumOf { it.summary.totalServices },
            totalDuration = dailyReports.sumOf { it.summary.totalDuration },
            totalDistance = dailyReports.sumOf { it.summary.totalDistance },
            averageAttendance = dailyReports.map { it.summary.attendanceRate }.average().toFloat(),
            bestDay = "Carsamba",
            worstDay = "Pazartesi",
            improvements = listOf("Dakiklik %3 artti", "Yakıt tuketimi iyilesti"),
            achievements = listOf("Tüm duraklar tamamlandi", "Sifir kaza bildirimi")
        )
        val trends = listOf(
            Trend("attendance", TrendDirection.UP, 4.2f, "Katılım oranı artış trendinde"),
            Trend("punctuality", TrendDirection.STABLE, 1.1f, "Dakiklik sabit seyretti"),
            Trend("efficiency", TrendDirection.UP, 2.4f, "Verimlilikte iyileşme var")
        )
        val report = WeeklyReport(
            weekStartDate = weekStartDate,
            weekEndDate = dateFormatter.format(LocalDate.parse(weekStartDate, dateFormatter).plusDays(6)),
            routeId = routeId,
            routeName = "Ornek Rota",
            dailyReports = dailyReports,
            weekSummary = weekSummary,
            trends = trends
        )
        return OzyuceResult.Success(report)
    }

    override suspend fun getReportSummary(filter: ReportFilter): OzyuceResult<ReportSummary> {
        val today = dateFormatter.format(LocalDate.now())
        return getDailyReport(today, filter.routeId ?: sampleServiceRepository.getAvailableRoutes().let { result ->
            if (result is OzyuceResult.Success) result.data.first().id else "route_1"
        }).map { it.summary }
    }

    override suspend fun getAttendanceChartData(filter: ReportFilter): OzyuceResult<ChartData> {
        val stops = sampleStopsRepository.currentStops()
        val totalPersonnel = stops.sumOf { it.personnelCount }
        val attended = stops.sumOf { it.checkedPersonnelCount }
        val absent = max(totalPersonnel - attended, 0)
        val chart = ChartData(
            type = ChartType.DONUT_CHART,
            title = "Katılım Oranları",
            data = listOf(
                ChartEntry("Katildi", attended.toFloat(), 0xFF4CAF50, "Servise katilan"),
                ChartEntry("Katilmadi", absent.toFloat(), 0xFFF44336, "Servise katilmayan")
            )
        )
        return OzyuceResult.Success(chart)
    }

    override suspend fun getPerformanceChartData(filter: ReportFilter): OzyuceResult<ChartData> {
        val chart = ChartData(
            type = ChartType.BAR_CHART,
            title = "Performans Göstergeleri",
            data = listOf(
                ChartEntry("Dakiklik", 94f),
                ChartEntry("Verimlilik", 89f),
                ChartEntry("Memnuniyet", 91f),
                ChartEntry("Tamamlama", 96f)
            )
        )
        return OzyuceResult.Success(chart)
    }

    override suspend fun getTimeAnalysisChartData(filter: ReportFilter): OzyuceResult<ChartData> {
        val labels = listOf("08:00", "08:30", "09:00", "09:30", "10:00")
        val chart = ChartData(
            type = ChartType.LINE_CHART,
            title = "Zaman Analizi",
            data = labels.mapIndexed { index, label ->
                ChartEntry(label, (35 + index * 3).toFloat())
            }
        )
        return OzyuceResult.Success(chart)
    }

    override suspend fun exportReportToPdf(report: DailyReport): OzyuceResult<String> {
        delay(80)
        return OzyuceResult.Success("/storage/emulated/0/Download/report_${report.date}.pdf")
    }

    override suspend fun getLateCount(filter: ReportFilter): OzyuceResult<Int> {
        return OzyuceResult.Success(5) // Ornek bir deger
    }

    private fun createSummary(totalPersonnel: Int, attended: Int): ReportSummary {
        return ReportSummary(
            totalServices = 3,
            totalDuration = 180,
            totalDistance = samplePolylineDistance(sampleStopsRepository.currentStops().map { LatLng(it.latitude, it.longitude) }),
            totalPersonnel = totalPersonnel,
            attendedPersonnel = attended,
            averageSpeed = 32.0,
            fuelConsumption = 18.0
        )
    }

    private inline fun <T, R> OzyuceResult<T>.map(transform: (T) -> R): OzyuceResult<R> = when (this) {
        is OzyuceResult.Success -> OzyuceResult.Success(transform(data))
        is OzyuceResult.Error -> OzyuceResult.Error(exception)
        OzyuceResult.Loading -> OzyuceResult.Loading
    }
}



private fun samplePolylineDistance(points: List<LatLng>): Double {
    if (points.size < 2) return 0.0
    return points.windowed(2).sumOf { (start, end) ->
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            start.latitude,
            start.longitude,
            end.latitude,
            end.longitude,
            results
        )
        results[0] / 1000.0
    }
}

private fun LocalDateTime.toDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
