package com.ozyuce.maps.feature.auth.data.remote

import com.ozyuce.maps.core.network.ApiResponse
import com.ozyuce.maps.feature.auth.data.remote.dto.AuthResponseDto
import com.ozyuce.maps.feature.auth.data.remote.dto.LoginRequestDto
import com.ozyuce.maps.feature.auth.data.remote.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Auth API endpoint'leri
 */
interface AuthApi {
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<ApiResponse<AuthResponseDto>>
    
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<ApiResponse<AuthResponseDto>>
    
    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>
}
