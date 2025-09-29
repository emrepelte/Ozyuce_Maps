package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.Result as AppResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<Unit>
    suspend fun register(email: String, password: String): AppResult<Unit>
    suspend fun logout(): AppResult<Unit>
}