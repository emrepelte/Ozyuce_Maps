package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.Stop
import com.ozyuce.maps.domain.repository.StopsRepository
import javax.inject.Inject

class GetStopsUseCase @Inject constructor(
    private val repo: StopsRepository
) {
    suspend operator fun invoke(routeId: String): AppResult<List<Stop>> = repo.getStops(routeId)
}