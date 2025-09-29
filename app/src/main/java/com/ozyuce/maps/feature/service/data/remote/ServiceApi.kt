package com.ozyuce.maps.feature.service.data.remote

import com.ozyuce.maps.core.network.ApiResponse
import com.ozyuce.maps.feature.service.data.remote.dto.ServiceSessionDto
import com.ozyuce.maps.feature.service.data.remote.dto.StartServiceRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Service API endpoint'leri
 */
interface ServiceApi {
    
    @POST("service/start")
    suspend fun startService(
        @Body request: StartServiceRequestDto
    ): Response<ApiResponse<ServiceSessionDto>>
    
    @POST("service/end")
    suspend fun endService(): Response<ApiResponse<Unit>>
    
    @GET("service/current")
    suspend fun getCurrentSession(): Response<ApiResponse<ServiceSessionDto>>
    
    @GET("service/history")
    suspend fun getServiceHistory(): Response<ApiResponse<List<ServiceSessionDto>>>
}
