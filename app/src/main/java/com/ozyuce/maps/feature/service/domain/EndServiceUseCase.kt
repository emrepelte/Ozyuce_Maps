package com.ozyuce.maps.feature.service.domain

import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import javax.inject.Inject

/**
 * Servis bitirme use case
 */
class EndServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(): Result<ServiceSession> {
        // Aktif servis kontrolu
        return when (val currentSession = serviceRepository.getCurrentSession()) {
            is Result.Success -> {
                val session = currentSession.data
                if (session?.isActive == true) {
                    serviceRepository.endService(session.id)
                } else {
                    Result.error(IllegalStateException("Aktif servis bulunamadi"))
                }
            }
            is Result.Error -> Result.error(IllegalStateException("Aktif servis durumu kontrol edilemedi", currentSession.exception))
            is Result.Loading -> Result.loading()
        }
    }
}
