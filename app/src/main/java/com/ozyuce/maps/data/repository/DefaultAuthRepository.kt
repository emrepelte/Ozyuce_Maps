package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthRepository @Inject constructor(
    private val authTokenProvider: AuthTokenProvider
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<Unit> = runCatching {
        val token = buildToken(email)
        authTokenProvider.setToken(token)
    }.toAppResult()

    override suspend fun register(email: String, password: String): AppResult<Unit> = runCatching {
        val token = buildToken(email)
        authTokenProvider.setToken(token)
    }.toAppResult()

    override suspend fun logout(): AppResult<Unit> = runCatching {
        authTokenProvider.setToken(null)
    }.toAppResult()

    private fun buildToken(seed: String): String = "token_${seed}_${System.currentTimeMillis()}"
}

private fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.success(it) },
    onFailure = { AppResult.error(it) }
)