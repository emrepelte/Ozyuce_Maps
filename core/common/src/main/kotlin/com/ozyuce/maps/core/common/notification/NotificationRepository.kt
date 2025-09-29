package com.ozyuce.maps.core.common.notification

import com.ozyuce.maps.core.common.result.Result
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun updateToken(token: String): Result<Unit>
    fun getToken(): Flow<Result<String?>>
}

