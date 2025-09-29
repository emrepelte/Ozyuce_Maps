package com.ozyuce.maps.data.network

import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthTokenInterceptor @Inject constructor(
    private val authTokenProvider: AuthTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking { authTokenProvider.getToken() }

        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}