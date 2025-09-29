package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.database.dao.ServiceDao
import com.ozyuce.maps.domain.repository.DailyReport
import com.ozyuce.maps.domain.repository.ReportsRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class DefaultReportsRepository @Inject constructor(
    private val serviceDao: ServiceDao,
    private val dispatcherProvider: DispatcherProvider
) : ReportsRepository {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault())

    override suspend fun getDailyReport(): AppResult<DailyReport> = runCatching {
        withContext(dispatcherProvider.io) {
            val services = serviceDao.getAll().first()
            val latestTimestamp = services.maxOfOrNull { it.startedAt ?: 0L } ?: System.currentTimeMillis()
            DailyReport(
                date = formatter.format(Instant.ofEpochMilli(latestTimestamp)),
                totalRides = services.size,
                onTimeRate = 1.0
            )
        }
    }.toAppResult()
}

private fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.success(it) },
    onFailure = { AppResult.error(it) }
)