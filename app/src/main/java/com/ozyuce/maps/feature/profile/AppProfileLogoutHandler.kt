package com.ozyuce.maps.feature.profile

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.usecase.LogoutUseCase
import com.ozyuce.maps.feature.profile.logout.ProfileLogoutHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppProfileLogoutHandler @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ProfileLogoutHandler {

    override suspend fun logout(): AppResult<Unit> = logoutUseCase()
}
