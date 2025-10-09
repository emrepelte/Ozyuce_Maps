package com.ozyuce.maps.feature.auth.domain

import com.ozyuce.maps.core.common.result.OzyuceResult

/**
 * Auth i?lemleri i?in repository interface
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): OzyuceResult<AuthResult>
    suspend fun register(email: String, password: String, name: String, role: String): OzyuceResult<AuthResult>
    suspend fun logout(): OzyuceResult<Unit>
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
