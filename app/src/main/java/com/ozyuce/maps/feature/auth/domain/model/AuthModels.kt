package com.ozyuce.maps.feature.auth.domain.model

/**
 * Kullan?c? domain modeli
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)

/**
 * Token domain modeli
 */
data class Token(
    val accessToken: String
)

/**
 * Auth result domain modeli
 */
data class AuthResult(
    val token: String,
    val user: User
)
