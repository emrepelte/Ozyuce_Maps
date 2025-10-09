package com.ozyuce.maps.feature.reports.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.reports.domain.model.DailyReport
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * G?nl?k rapor alma use case
 */
class GetDailyReportUseCase @Inject constructor(
    private val reportsRepository: ReportsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(date: String, routeId: String): OzyuceResult<DailyReport> {
        return withContext(dispatcherProvider.io) {
            // Validation
            if (date.isBlank()) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Tarih bilgisi gereklidir"))
            }
            
            if (routeId.isBlank()) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Rota bilgisi gereklidir"))
            }
            
            reportsRepository.getDailyReport(date, routeId)
        }
    }
}
