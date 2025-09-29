package com.ozyuce.maps.feature.auth.domain

import com.ozyuce.maps.core.common.result.Result

/**
 * Auth i?lemleri i?in repository interface
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(email: String, password: String, name: String, role: String): Result<AuthResult>
    suspend fun logout(): Result<Unit>
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUserId(): String?
    suspend fun getCurrentUserRole(): String?
}

/**
 * Auth sonu? modeli
 */
data class AuthResult(
    val token: String,
    val user: User
)

/**
 * User domain modeli
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String // "driver" veya "customer"
)
