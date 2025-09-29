package com.ozyuce.maps.core.common.auth

interface AuthTokenProvider {
    suspend fun getToken(): String?
    suspend fun setToken(token: String?)
}