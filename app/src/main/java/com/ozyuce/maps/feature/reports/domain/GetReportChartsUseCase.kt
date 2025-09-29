package com.ozyuce.maps.feature.reports.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.reports.domain.model.ChartData
import com.ozyuce.maps.feature.reports.domain.model.ReportFilter
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Rapor grafik verilerini getiren Use Case.
 */
class GetReportChartsUseCase @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend fun getAttendanceChart(filter: ReportFilter): Result<ChartData> {
        return withContext(dispatcherProvider.io) {
            reportsRepository.getAttendanceChartData(filter)
        }
    }
    
    suspend fun getPerformanceChart(filter: ReportFilter): Result<ChartData> {
        return withContext(dispatcherProvider.io) {
            reportsRepository.getPerformanceChartData(filter)
        }
    }
    
    suspend fun getTimeAnalysisChart(filter: ReportFilter): Result<ChartData> {
        return withContext(dispatcherProvider.io) {
            reportsRepository.getTimeAnalysisChartData(filter)
        }
    }
}
