package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.DailyReport
import com.ozyuce.maps.domain.repository.ReportsRepository
import javax.inject.Inject

class GetDailyReportUseCase @Inject constructor(
    private val repo: ReportsRepository
) {
    suspend operator fun invoke(): AppResult<DailyReport> = repo.getDailyReport()
}