package com.ozyuce.maps.core.network

import com.ozyuce.maps.core.common.result.Result
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Network i?lemleri i?in yard?mc? fonksiyonlar
 */
object NetworkUtils {
    
    /**
     * Retrofit Response'? Result<T>'ye d?n??t?r?r
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    Result.Success(data)
                } ?: Result.Error(Exception("Response body is null"))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: Exception) {
            Result.Error(mapNetworkException(e))
        }
    }
    
    /**
     * ApiResponse<T> format?ndaki response'lar? Result<T>'ye d?n??t?r?r
     */
    suspend fun <T> safeApiCallWithWrapper(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        apiResponse.data?.let { data ->
                            Result.Success(data)
                        } ?: Result.Error(Exception("Data is null"))
                    } else {
                        val error = apiResponse.error
                        Result.Error(
                            if (error != null) {
                                NetworkException.ApiError(error)
                            } else {
                                Exception(apiResponse.message ?: "Unknown API error")
                            }
                        )
                    }
                } ?: Result.Error(Exception("Response body is null"))
            } else {
                Result.Error(
                    NetworkException.ServerError(
                        response.code(),
                        response.message()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(mapNetworkException(e))
        }
    }
    
    /**
     * Network exception'lar?n? daha anlaml? hata mesajlar?na d?n??t?r?r
     */
    private fun mapNetworkException(exception: Exception): NetworkException {
        return when (exception) {
            is UnknownHostException -> NetworkException.NetworkError(
                "?nternet ba?lant?n?z? kontrol edin", exception
            )
            is SocketTimeoutException -> NetworkException.NetworkError(
                "Ba?lant? zaman a??m?na u?rad?", exception
            )
            is IOException -> NetworkException.NetworkError(
                "A? hatas? olu?tu", exception
            )
            is HttpException -> NetworkException.ServerError(
                exception.code(), 
                exception.message()
            )
            else -> NetworkException.UnknownError(
                exception.message ?: "Bilinmeyen hata", exception
            )
        }
    }
    
    // Ge?ici olarak kapat?ld? - Demo mode i?in API ?a?r?s? yap?lm?yor
    /*
    suspend fun <T> safeApiCallWithWrapper(apiCall: suspend () -> retrofit2.Response<com.ozyuce.maps.core.network.ApiResponse<T>>): Result<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                when {
                    body == null -> Result.Error(Exception("Bo? yan?t al?nd?"))
                    body is com.ozyuce.maps.core.network.ApiResponse.Success -> Result.Success(body.data)
                    body is com.ozyuce.maps.core.network.ApiResponse.Error -> Result.Error(Exception(body.message ?: "API hatas?"))
                    else -> Result.Error(Exception("Bilinmeyen API yan?t?"))
                }
            } else {
                Result.Error(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(mapNetworkException(e))
        }
    }
    */

    /**
     * Hata mesajlar?n? kullan?c? dostu hale getirir
     */
    fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is NetworkException.NetworkError -> exception.message ?: "Network hatas?"
            is NetworkException.ServerError -> when (exception.code) {
                401 -> "Oturum s?reniz dolmu?, l?tfen tekrar giri? yap?n"
                403 -> "Bu i?lem i?in yetkiniz bulunmuyor"
                404 -> "?stenen kaynak bulunamad?"
                500 -> "Sunucu hatas?, l?tfen daha sonra tekrar deneyin"
                else -> "Sunucu hatas?: ${exception.message}"
            }
            is NetworkException.ApiError -> exception.error.message
            else -> exception.message ?: "Bilinmeyen hata olu?tu"
        }
    }
}
