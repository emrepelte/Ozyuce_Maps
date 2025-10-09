package com.ozyuce.maps.feature.reports.domain

import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.reports.domain.model.DailyReport
import com.ozyuce.maps.feature.reports.domain.model.WeeklyReport
import com.ozyuce.maps.feature.reports.domain.model.ChartData
import com.ozyuce.maps.feature.reports.domain.model.ReportFilter
import com.ozyuce.maps.feature.reports.domain.model.ReportSummary

/**
 * Raporlar i?in repository interface
 */
interface ReportsRepository {
    suspend fun getDailyReport(date: String, routeId: String): OzyuceResult<DailyReport>
    suspend fun getWeeklyReport(weekStartDate: String, routeId: String): OzyuceResult<WeeklyReport>
    suspend fun getReportSummary(filter: ReportFilter): OzyuceResult<ReportSummary>
    suspend fun getAttendanceChartData(filter: ReportFilter): OzyuceResult<ChartData>
    suspend fun getPerformanceChartData(filter: ReportFilter): OzyuceResult<ChartData>
    suspend fun getTimeAnalysisChartData(filter: ReportFilter): OzyuceResult<ChartData>
    suspend fun getLateCount(filter: ReportFilter): OzyuceResult<Int>
    suspend fun exportReportToPdf(report: DailyReport): OzyuceResult<String> // File path
}

// Modeller art?k model/ReportsModels.kt dosyas?nda

