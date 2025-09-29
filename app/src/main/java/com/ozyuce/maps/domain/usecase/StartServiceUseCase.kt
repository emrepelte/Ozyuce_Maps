package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.ServiceRepository
import javax.inject.Inject

class StartServiceUseCase @Inject constructor(
    private val repo: ServiceRepository
) {
    suspend operator fun invoke(routeId: String): AppResult<Unit> = repo.startService(routeId)
}