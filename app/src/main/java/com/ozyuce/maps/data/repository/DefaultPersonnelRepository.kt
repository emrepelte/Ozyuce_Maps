package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.database.dao.PersonDao
import com.ozyuce.maps.data.mapper.toDomainPersonnel
import com.ozyuce.maps.data.mapper.toEntity
import com.ozyuce.maps.domain.repository.Personnel
import com.ozyuce.maps.domain.repository.PersonnelRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class DefaultPersonnelRepository @Inject constructor(
    private val personDao: PersonDao,
    private val dispatcherProvider: DispatcherProvider
) : PersonnelRepository {

    override suspend fun getPersonnel(routeId: String): AppResult<List<Personnel>> = runCatching {
        withContext(dispatcherProvider.io) {
            // Currently returns all personnel regardless of route
            personDao.getAll().first().map { it.toDomainPersonnel() }
        }
    }.toAppResult()

    override suspend fun addPersonnel(personnel: Personnel): AppResult<Unit> = runCatching {
        withContext(dispatcherProvider.io) {
            personDao.insert(personnel.toEntity())
        }
    }.toAppResult()
}

private fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.success(it) },
    onFailure = { AppResult.error(it) }
)