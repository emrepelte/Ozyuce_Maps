package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.database.dao.StopDao
import com.ozyuce.maps.data.mapper.toDomainStop
import com.ozyuce.maps.domain.repository.Stop
import com.ozyuce.maps.domain.repository.StopsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

@Singleton
class DefaultStopsRepository @Inject constructor(
    private val stopDao: StopDao,
    private val dispatcherProvider: DispatcherProvider
) : StopsRepository {

    override suspend fun getStops(routeId: String): AppResult<List<Stop>> = runCatching {
        withContext(dispatcherProvider.io) {
            val stops = stopDao.getStopsByServiceId(routeId).firstOrNull()
                ?: stopDao.getAll().first()
            stops.mapIndexed { index, entity -> entity.toDomainStop(index) }
        }
    }.toAppResult()

    override suspend fun checkStop(stopId: String, boarded: Boolean): AppResult<Unit> = runCatching {
        withContext(dispatcherProvider.io) {
            // TODO: Persist check information - placeholder implementation
        }
    }.toAppResult()
}

private fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.success(it) },
    onFailure = { AppResult.error(it) }
)