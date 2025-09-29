package com.ozyuce.maps.core.database.sync

import com.ozyuce.maps.core.database.dao.PersonDao
import com.ozyuce.maps.core.database.dao.ServiceDao
import com.ozyuce.maps.core.database.dao.StopDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SyncUseCase @Inject constructor(
    private val personDao: PersonDao,
    private val stopDao: StopDao,
    private val serviceDao: ServiceDao
) {
    fun sync(): Flow<SyncStatus> = flow {
        emit(SyncStatus.Started)

        try {
            // Get all unsynced entities
            val unsyncedPersons = personDao.getUnsynced()
            val unsyncedStops = stopDao.getUnsynced()
            val unsyncedServices = serviceDao.getUnsynced()

            // TODO: Send to server
            // For now, just mark as synced
            val currentTime = System.currentTimeMillis()
            
            unsyncedPersons.forEach { person ->
                personDao.markSynced(person.id, currentTime)
            }
            
            unsyncedStops.forEach { stop ->
                stopDao.markSynced(stop.id, currentTime)
            }
            
            unsyncedServices.forEach { service ->
                serviceDao.markSynced(service.id, currentTime)
            }

            emit(SyncStatus.Success)
        } catch (e: Exception) {
            emit(SyncStatus.Error(e.message ?: "Sync failed"))
        }
    }
}

sealed interface SyncStatus {
    data object Started : SyncStatus
    data object Success : SyncStatus
    data class Error(val message: String) : SyncStatus
}
