package com.ozyuce.maps.feature.reports.domain

import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.reports.domain.model.DailyReport
import com.ozyuce.maps.feature.reports.domain.model.WeeklyReport
import com.ozyuce.maps.feature.reports.domain.model.ChartData
import com.ozyuce.maps.feature.reports.domain.model.ReportFilter
import com.ozyuce.maps.feature.reports.domain.model.ReportSummary

/**
 * Raporlar i?in repository interface
 */
interface ReportsRepository {
    suspend fun getDailyReport(date: String, routeId: String): Result<DailyReport>
    suspend fun getWeeklyReport(weekStartDate: String, routeId: String): Result<WeeklyReport>
    suspend fun getReportSummary(filter: ReportFilter): Result<ReportSummary>
    suspend fun getAttendanceChartData(filter: ReportFilter): Result<ChartData>
    suspend fun getPerformanceChartData(filter: ReportFilter): Result<ChartData>
    suspend fun getTimeAnalysisChartData(filter: ReportFilter): Result<ChartData>
    suspend fun getLateCount(filter: ReportFilter): Result<Int>
    suspend fun exportReportToPdf(report: DailyReport): Result<String> // File path
}

// Modeller art?k model/ReportsModels.kt dosyas?nda

