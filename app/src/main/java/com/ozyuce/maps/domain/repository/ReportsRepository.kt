package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.Result as AppResult

data class DailyReport(
    val date: String,
    val totalRides: Int,
    val onTimeRate: Double
)

interface ReportsRepository {
    suspend fun getDailyReport(): AppResult<DailyReport>
}