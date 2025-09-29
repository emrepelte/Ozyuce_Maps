package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.database.dao.ServiceDao
import com.ozyuce.maps.core.database.entity.ServiceEntity
import com.ozyuce.maps.domain.repository.ServiceRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

@Singleton
class DefaultServiceRepository @Inject constructor(
    private val serviceDao: ServiceDao,
    private val dispatcherProvider: DispatcherProvider
) : ServiceRepository {

    override suspend fun startService(routeId: String): AppResult<Unit> = runCatching {
        withContext(dispatcherProvider.io) {
            val entity = ServiceEntity(
                id = routeId,
                routeName = routeId,
                vehiclePlate = "",
                startedAt = System.currentTimeMillis(),
                endedAt = null
            )
            serviceDao.insert(entity)
        }
    }.toAppResult()

    override suspend fun endService(): AppResult<Unit> = runCatching {
        withContext(dispatcherProvider.io) {
            val active = serviceDao.getActiveService().firstOrNull()
            if (active != null) {
                serviceDao.endService(active.id, System.currentTimeMillis())
            }
        }
    }.toAppResult()
}

private fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.success(it) },
    onFailure = { AppResult.error(it) }
)