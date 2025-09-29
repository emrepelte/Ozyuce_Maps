package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.Result as AppResult

interface NotificationsRepository {
    suspend fun saveFcmToken(token: String): AppResult<Unit>
}