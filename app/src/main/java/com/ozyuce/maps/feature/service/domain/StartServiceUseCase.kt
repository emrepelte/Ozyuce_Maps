package com.ozyuce.maps.feature.service.domain

import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import javax.inject.Inject

/**
 * Servis ba?latma use case
 */
class StartServiceUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) {
    suspend operator fun invoke(routeId: String): Result<ServiceSession> {
        if (routeId.isBlank()) {
            return Result.Error(IllegalArgumentException("Rota ID bo? olamaz"))
        }
        
        // Aktif servis kontrol?
        return when (val currentSession = serviceRepository.getCurrentSession()) {
            is Result.Success -> {
                if (currentSession.data?.isActive == true) {
                    Result.Error(IllegalStateException("Zaten aktif bir servis bulunmaktad?r"))
                } else {
                    serviceRepository.startService(routeId)
                }
            }
            is Result.Error -> serviceRepository.startService(routeId)
            is Result.Loading -> Result.Loading
        }
    }
}
