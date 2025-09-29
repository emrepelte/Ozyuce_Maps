package com.ozyuce.maps.feature.reports.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.reports.domain.model.WeeklyReport
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Haftal?k raporu getiren Use Case.
 */
class GetWeeklyReportUseCase @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(weekStartDate: String, routeId: String): Result<WeeklyReport> {
        return withContext(dispatcherProvider.io) {
            // Validation
            if (weekStartDate.isBlank()) {
                return@withContext Result.Error(IllegalArgumentException("Hafta ba?lang?? tarihi gereklidir"))
            }
            
            if (routeId.isBlank()) {
                return@withContext Result.Error(IllegalArgumentException("Rota bilgisi gereklidir"))
            }
            
            reportsRepository.getWeeklyReport(weekStartDate, routeId)
        }
    }
}
