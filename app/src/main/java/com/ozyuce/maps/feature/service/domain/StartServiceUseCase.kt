package com.ozyuce.maps.feature.service.domain

import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import javax.inject.Inject

/**
 * Servis ba?latma use case
 */
class StartServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(routeId: String): OzyuceResult<ServiceSession> {
        if (routeId.isBlank()) {
            return OzyuceResult.Error(IllegalArgumentException("Rota ID bo? olamaz"))
        }
        
        // Aktif servis kontrol?
        return when (val currentSession = serviceRepository.getCurrentSession()) {
            is OzyuceResult.Success -> {
                if (currentSession.data?.isActive == true) {
                    OzyuceResult.Error(IllegalStateException("Zaten aktif bir servis bulunmaktad?r"))
                } else {
                    serviceRepository.startService(routeId)
                }
            }
            is OzyuceResult.Error -> serviceRepository.startService(routeId)
            is OzyuceResult.Loading -> OzyuceResult.Loading
        }
    }
}
