package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(): AppResult<Unit> = repo.logout()
}