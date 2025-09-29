package com.ozyuce.maps.core.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * API'dan gelen standart response format?
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "data")
    val data: T? = null,
    @Json(name = "error")
    val error: ApiError? = null
)

@JsonClass(generateAdapter = true)
data class ApiError(
    @Json(name = "code")
    val code: String,
    @Json(name = "message")
    val message: String,
    @Json(name = "details")
    val details: Map<String, Any>? = null
)

/**
 * Network hatalar?n? handle etmek i?in exception s?n?flar?
 */
sealed class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkError(message: String, cause: Throwable? = null) : NetworkException(message, cause)
    class ServerError(val code: Int, message: String) : NetworkException(message)
    class ApiError(val error: com.ozyuce.maps.core.network.ApiError) : NetworkException(error.message)
    class UnknownError(message: String, cause: Throwable? = null) : NetworkException(message, cause)
}
