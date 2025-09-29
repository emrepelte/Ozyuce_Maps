package com.ozyuce.maps.core.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ozyuce.maps.core.common.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HTTP isteklerine otomatik olarak Authorization header ekler
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {
    
    private val tokenKey = stringPreferencesKey(Constants.TOKEN_KEY)
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Token gerektirmeyen endpoint'ler i?in kontrol
        if (originalRequest.url.pathSegments.contains("login") || 
            originalRequest.url.pathSegments.contains("register")) {
            return chain.proceed(originalRequest)
        }
        
        // Token'? DataStore'dan al
        val token = runBlocking {
            dataStore.data.map { preferences ->
                preferences[tokenKey]
            }.first()
        }
        
        // Token varsa Authorization header ekle
        val requestBuilder = originalRequest.newBuilder()
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
