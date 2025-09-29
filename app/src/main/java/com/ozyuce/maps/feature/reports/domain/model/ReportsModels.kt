package com.ozyuce.maps.feature.reports.domain.model

import java.util.Date

/**
 * G?nl?k rapor modeli
 */
data class DailyReport(
    val date: String, // yyyy-MM-dd format
    val routeId: String,
    val routeName: String,
    val driverName: String,
    val summary: ReportSummary,
    val attendanceData: AttendanceData,
    val timeAnalysis: TimeAnalysis,
    val performanceMetrics: PerformanceMetrics
)

/**
 * Haftal?k rapor modeli
 */
data class WeeklyReport(
    val weekStartDate: String,
    val weekEndDate: String,
    val routeId: String,
    val routeName: String,
    val dailyReports: List<DailyReport>,
    val weekSummary: WeeklySummary,
    val trends: List<Trend>
)

/**
 * Rapor ?zeti
 */
data class ReportSummary(
    val totalServices: Int,
    val totalDuration: Long, // dakika
    val totalDistance: Double, // km
    val totalPersonnel: Int,
    val attendedPersonnel: Int,
    val averageSpeed: Double, // km/h
    val fuelConsumption: Double? = null // litre
) {
    val attendanceRate: Float
        get() = if (totalPersonnel > 0) {
            (attendedPersonnel.toFloat() / totalPersonnel.toFloat()) * 100f
        } else 0f
        
    val averageDuration: Double
        get() = if (totalServices > 0) {
            totalDuration.toDouble() / totalServices.toDouble()
        } else 0.0
}

/**
 * Kat?l?m verileri
 */
data class AttendanceData(
    val attendedCount: Int,
    val absentCount: Int,
    val lateCount: Int,
    val earlyLeaveCount: Int
) {
    val totalCount: Int
        get() = attendedCount + absentCount
        
    val attendanceRate: Float
        get() = if (totalCount > 0) {
            (attendedCount.toFloat() / totalCount.toFloat()) * 100f
        } else 0f
}

/**
 * Zaman analizi
 */
data class TimeAnalysis(
    val earliestStart: String, // HH:mm format
    val latestEnd: String, // HH:mm format
    val averageServiceDuration: Double, // dakika
    val onTimePerformance: Float, // y?zde
    val delayMinutes: Double // ortalama gecikme dakika
)

/**
 * Performans metrikleri
 */
data class PerformanceMetrics(
    val punctualityScore: Float, // 0-100
    val efficiencyScore: Float, // 0-100
    val customerSatisfaction: Float, // 0-100
    val fuelEfficiency: Float? = null, // km/litre
    val stopCompleteRate: Float, // 0-100
    val overallScore: Float // 0-100
) {
    companion object {
        fun calculateOverallScore(
            punctuality: Float,
            efficiency: Float,
            satisfaction: Float,
            stopComplete: Float
        ): Float {
            return (punctuality + efficiency + satisfaction + stopComplete) / 4f
        }
    }
}

/**
 * Haftal?k ?zet
 */
data class WeeklySummary(
    val totalServices: Int,
    val totalDuration: Long,
    val totalDistance: Double,
    val averageAttendance: Float,
    val bestDay: String,
    val worstDay: String,
    val improvements: List<String>,
    val achievements: List<String>
)

/**
 * Trend verisi
 */
data class Trend(
    val metric: String, // "attendance", "punctuality", "efficiency"
    val trend: TrendDirection,
    val percentage: Float, // de?i?im y?zdesi
    val description: String
)

/**
 * Trend y?n?
 */
enum class TrendDirection {
    UP,
    DOWN,
    STABLE
}

/**
 * Grafik veri modeli
 */
data class ChartData(
    val type: ChartType,
    val title: String,
    val data: List<ChartEntry>
)

/**
 * Grafik giri?i
 */
data class ChartEntry(
    val label: String,
    val value: Float,
    val color: Long? = null,
    val description: String? = null
)

/**
 * Grafik t?r?
 */
enum class ChartType {
    PIE_CHART,      // Pie chart - kat?l?m oranlar?
    BAR_CHART,      // Bar chart - g?nl?k istatistikler
    LINE_CHART,     // Line chart - trend analizi
    DONUT_CHART     // Donut chart - performans da??l?m?
}

/**
 * Rapor filtresi
 */
data class ReportFilter(
    val startDate: String,
    val endDate: String,
    val routeId: String? = null,
    val driverId: String? = null,
    val reportType: ReportType = ReportType.DAILY
)

/**
 * Rapor t?r?
 */
enum class ReportType {
    DAILY,
    WEEKLY,
    MONTHLY
}
