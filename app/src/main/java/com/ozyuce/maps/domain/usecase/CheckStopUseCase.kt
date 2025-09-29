package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.StopsRepository
import javax.inject.Inject

class CheckStopUseCase @Inject constructor(
    private val repo: StopsRepository
) {
    suspend operator fun invoke(stopId: String, boarded: Boolean): AppResult<Unit> =
        repo.checkStop(stopId, boarded)
}