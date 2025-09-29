package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.NotificationsRepository
import javax.inject.Inject

class SaveFcmTokenUseCase @Inject constructor(
    private val repo: NotificationsRepository
) {
    suspend operator fun invoke(token: String): AppResult<Unit> =
        repo.saveFcmToken(token)
}