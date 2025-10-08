package com.ozyuce.maps.feature.reports.data.repository

import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.reports.domain.ReportsRepository
import com.ozyuce.maps.feature.reports.domain.model.DailyReport
import com.ozyuce.maps.feature.reports.domain.model.WeeklyReport
import com.ozyuce.maps.feature.reports.domain.model.ChartData
import com.ozyuce.maps.feature.reports.domain.model.ReportFilter
import com.ozyuce.maps.feature.reports.domain.model.ReportSummary
import com.ozyuce.maps.feature.reports.domain.model.AttendanceData
import com.ozyuce.maps.feature.reports.domain.model.TimeAnalysis
import com.ozyuce.maps.feature.reports.domain.model.PerformanceMetrics
import com.ozyuce.maps.feature.reports.domain.model.WeeklySummary
import com.ozyuce.maps.feature.reports.domain.model.Trend
import com.ozyuce.maps.feature.reports.domain.model.TrendDirection
import com.ozyuce.maps.feature.reports.domain.model.ChartType
import com.ozyuce.maps.feature.reports.domain.model.ChartEntry
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * ReportsRepository implementasyonu - Demo mode
 */
@Singleton
class ReportsRepositoryImpl @Inject constructor() : ReportsRepository {

    override suspend fun getDailyReport(date: String, routeId: String): Result<DailyReport> {
        return try {
            kotlinx.coroutines.delay(800) // Network simulation
            
            val mockReport = generateMockDailyReport(date, routeId)
            Result.Success(mockReport)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getWeeklyReport(weekStartDate: String, routeId: String): Result<WeeklyReport> {
        return try {
            kotlinx.coroutines.delay(1200) // Network simulation
            
            val mockReport = generateMockWeeklyReport(weekStartDate, routeId)
            Result.Success(mockReport)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getReportSummary(filter: ReportFilter): Result<ReportSummary> {
        return try {
            kotlinx.coroutines.delay(600) // Network simulation
            
            val mockSummary = generateMockReportSummary()
            Result.Success(mockSummary)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAttendanceChartData(filter: ReportFilter): Result<ChartData> {
        return try {
            kotlinx.coroutines.delay(400) // Network simulation
            
            val attendanceChart = ChartData(
                type = ChartType.PIE_CHART,
                title = "Kat?l?m Oranlar?",
                data = listOf(
                    ChartEntry("Kat?ld?", 78f, 0xFF4CAF50, "Servise kat?lan personel"),
                    ChartEntry("Kat?lmad?", 15f, 0xFFF44336, "Servise kat?lmayan personel"),
                    ChartEntry("Ge? Kat?ld?", 7f, 0xFFFF9800, "Ge? kat?lan personel")
                )
            )
            
            Result.Success(attendanceChart)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getPerformanceChartData(filter: ReportFilter): Result<ChartData> {
        return try {
            kotlinx.coroutines.delay(500) // Network simulation
            
            val performanceChart = ChartData(
                type = ChartType.BAR_CHART,
                title = "Performans Metrikleri",
                data = listOf(
                    ChartEntry("Dakiklik", 85f, 0xFF2196F3, "Zaman?nda var?? oran?"),
                    ChartEntry("Verimlilik", 92f, 0xFF4CAF50, "Servis tamamlama oran?"),
                    ChartEntry("M??teri Memnuniyeti", 88f, 0xFFFF9800, "Memnuniyet puan?"),
                    ChartEntry("Durak Tamamlama", 95f, 0xFF9C27B0, "Durak tamamlama oran?")
                )
            )
            
            Result.Success(performanceChart)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getTimeAnalysisChartData(filter: ReportFilter): Result<ChartData> {
        return try {
            kotlinx.coroutines.delay(450) // Network simulation

            val timeChart = ChartData(
                type = ChartType.LINE_CHART,
                title = "Haftalık Zaman Analizi",
                data = listOf(
                    ChartEntry("Pazartesi", 42f, description = "42 dakika"),
                    ChartEntry("Salı", 38f, description = "38 dakika"),
                    ChartEntry("Çarşamba", 45f, description = "45 dakika"),
                    ChartEntry("Perşembe", 40f, description = "40 dakika"),
                    ChartEntry("Cuma", 43f, description = "43 dakika"),
                    ChartEntry("Cumartesi", 35f, description = "35 dakika"),
                    ChartEntry("Pazar", 30f, description = "30 dakika")
                )
            )

            Result.Success(timeChart)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getLateCount(filter: ReportFilter): Result<Int> {
        return try {
            kotlinx.coroutines.delay(200)
            Result.Success(Random.nextInt(0, 50))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun exportReportToPdf(report: DailyReport): Result<String> {
        return try {
            kotlinx.coroutines.delay(2000) // File generation simulation
            
            // Mock file path
            val filePath = "/storage/emulated/0/Download/report_${report.date}.pdf"
            Result.Success(filePath)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun generateMockDailyReport(date: String, routeId: String): DailyReport {
        val totalPersonnel = Random.nextInt(20, 35)
        val attendedPersonnel = Random.nextInt((totalPersonnel * 0.7).toInt(), totalPersonnel)
        
        return DailyReport(
            date = date,
            routeId = routeId,
            routeName = getRouteName(routeId),
            driverName = "Demo S?r?c?",
            summary = ReportSummary(
                totalServices = Random.nextInt(2, 4),
                totalDuration = Random.nextLong(120, 200),
                totalDistance = Random.nextDouble(15.0, 45.0),
                totalPersonnel = totalPersonnel,
                attendedPersonnel = attendedPersonnel,
                averageSpeed = Random.nextDouble(25.0, 40.0),
                fuelConsumption = Random.nextDouble(5.0, 12.0)
            ),
            attendanceData = AttendanceData(
                attendedCount = attendedPersonnel,
                absentCount = totalPersonnel - attendedPersonnel,
                lateCount = Random.nextInt(0, 3),
                earlyLeaveCount = Random.nextInt(0, 2)
            ),
            timeAnalysis = TimeAnalysis(
                earliestStart = "07:30",
                latestEnd = "18:45",
                averageServiceDuration = Random.nextDouble(35.0, 50.0),
                onTimePerformance = Random.nextDouble(80.0, 95.0).toFloat(),
                delayMinutes = Random.nextDouble(2.0, 8.0)
            ),
            performanceMetrics = PerformanceMetrics(
                punctualityScore = Random.nextDouble(75.0, 95.0).toFloat(),
                efficiencyScore = Random.nextDouble(80.0, 98.0).toFloat(),
                customerSatisfaction = Random.nextDouble(85.0, 96.0).toFloat(),
                fuelEfficiency = Random.nextDouble(3.5, 6.2).toFloat(),
                stopCompleteRate = Random.nextDouble(90.0, 100.0).toFloat(),
                overallScore = 0f // Hesaplanacak
            ).let { metrics ->
                metrics.copy(
                    overallScore = PerformanceMetrics.calculateOverallScore(
                        metrics.punctualityScore,
                        metrics.efficiencyScore,
                        metrics.customerSatisfaction,
                        metrics.stopCompleteRate
                    )
                )
            }
        )
    }

    private fun generateMockWeeklyReport(weekStartDate: String, routeId: String): WeeklyReport {
        val dailyReports = (0..6).map { dayOffset ->
            generateMockDailyReport("2024-01-${15 + dayOffset}", routeId)
        }
        
        return WeeklyReport(
            weekStartDate = weekStartDate,
            weekEndDate = "2024-01-21", // Mock end date
            routeId = routeId,
            routeName = getRouteName(routeId),
            dailyReports = dailyReports,
            weekSummary = WeeklySummary(
                totalServices = dailyReports.sumOf { it.summary.totalServices },
                totalDuration = dailyReports.sumOf { it.summary.totalDuration },
                totalDistance = dailyReports.sumOf { it.summary.totalDistance },
                averageAttendance = dailyReports.map { it.summary.attendanceRate }.average().toFloat(),
                bestDay = "?ar?amba",
                worstDay = "Pazartesi",
                improvements = listOf(
                    "Dakiklik oran? %5 artt?",
                    "Yak?t verimlili?i iyile?ti",
                    "M??teri memnuniyeti y?kseldi"
                ),
                achievements = listOf(
                    "Haftal?k hedef a??ld?",
                    "S?f?r kaza kayd?",
                    "T?m duraklar tamamland?"
                )
            ),
            trends = listOf(
                Trend("attendance", TrendDirection.UP, 5.2f, "Kat?l?m oran? art?? g?steriyor"),
                Trend("punctuality", TrendDirection.UP, 3.1f, "Dakiklik performans? iyile?iyor"),
                Trend("efficiency", TrendDirection.STABLE, 0.8f, "Verimlilik stabil seyrediyor")
            )
        )
    }

    private fun generateMockReportSummary(): ReportSummary {
        val totalPersonnel = Random.nextInt(150, 200)
        val attendedPersonnel = Random.nextInt((totalPersonnel * 0.75).toInt(), totalPersonnel)
        
        return ReportSummary(
            totalServices = Random.nextInt(15, 25),
            totalDuration = Random.nextLong(800, 1200),
            totalDistance = Random.nextDouble(120.0, 300.0),
            totalPersonnel = totalPersonnel,
            attendedPersonnel = attendedPersonnel,
            averageSpeed = Random.nextDouble(28.0, 38.0),
            fuelConsumption = Random.nextDouble(40.0, 80.0)
        )
    }

    private fun getRouteName(routeId: String): String {
        return when (routeId) {
            "route_1" -> "Ana Kamp?s Rotas?"
            "route_2" -> "?ehir ??i Rotas?"
            "route_3" -> "Sanayi Rotas?"
            else -> "Bilinmeyen Rota"
        }
    }
}

