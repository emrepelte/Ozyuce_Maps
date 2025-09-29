package com.ozyuce.maps.feature.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Login request DTO
 */
@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email")
    val email: String,
    @Json(name = "password")
    val password: String
)

/**
 * Register request DTO
 */
@JsonClass(generateAdapter = true)
data class RegisterRequestDto(
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "password")
    val password: String,
    @Json(name = "role")
    val role: String
)
