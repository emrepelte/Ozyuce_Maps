package com.ozyuce.maps.feature.auth.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Auth response DTO
 */
@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "token")
    val token: String,
    @Json(name = "user")
    val user: UserDto
)

/**
 * User DTO
 */
@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "role")
    val role: String
)
