package com.ozyuce.maps.feature.service.domain

import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import javax.inject.Inject

/**
 * Servis bitirme use case
 */
class EndServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(): OzyuceResult<ServiceSession> {
        // Aktif servis kontrolu
        return when (val currentSession = serviceRepository.getCurrentSession()) {
            is OzyuceResult.Success -> {
                val session = currentSession.data
                if (session?.isActive == true) {
                    serviceRepository.endService(session.id)
                } else {
                    OzyuceResult.error(IllegalStateException("Aktif servis bulunamadi"))
                }
            }
            is OzyuceResult.Error -> OzyuceResult.error(IllegalStateException("Aktif servis durumu kontrol edilemedi", currentSession.exception))
            is OzyuceResult.Loading -> OzyuceResult.loading()
        }
    }
}
