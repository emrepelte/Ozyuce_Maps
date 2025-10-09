package com.ozyuce.maps.feature.auth.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ozyuce.maps.core.common.Constants
import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.core.network.NetworkUtils
import com.ozyuce.maps.feature.auth.data.mapper.AuthMapper
import com.ozyuce.maps.feature.auth.data.remote.AuthApi
import com.ozyuce.maps.feature.auth.domain.AuthRepository
import com.ozyuce.maps.feature.auth.domain.AuthResult
import com.ozyuce.maps.feature.auth.domain.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthRepository implementasyonu
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val dataStore: DataStore<Preferences>,
    private val dispatchers: DispatcherProvider
) : AuthRepository {
    
    private val tokenKey = stringPreferencesKey(Constants.TOKEN_KEY)
    private val userIdKey = stringPreferencesKey(Constants.USER_ID_KEY)
    private val userRoleKey = stringPreferencesKey(Constants.USER_ROLE_KEY)
    
    override suspend fun login(email: String, password: String): OzyuceResult<AuthResult> {
        return withContext(dispatchers.io) {
            // Demo mode - ger?ek API ?a?r?s? yerine mock data
            try {
                kotlinx.coroutines.delay(1000) // Simulated network delay
                
                if (email == "test@example.com" && password == "password123") {
                    val mockAuthResult = AuthResult(
                        token = "demo_jwt_token_${System.currentTimeMillis()}",
                        user = User(
                            id = "demo_user_id",
                            name = "Demo Kullan?c?",
                            email = email,
                            role = "driver" // Demo i?in driver olarak set ediyoruz
                        )
                    )
                    
                    // Save mock auth data
                    dataStore.edit { preferences ->
                        preferences[tokenKey] = mockAuthResult.token
                        preferences[userIdKey] = mockAuthResult.user.id
                        preferences[userRoleKey] = mockAuthResult.user.role
                    }
                    
                    OzyuceResult.Success(mockAuthResult)
                } else {
                    OzyuceResult.Error(Exception("Ge?ersiz email veya ?ifre"))
                }
            } catch (e: Exception) {
                OzyuceResult.Error(e)
            }
        }
    }
    
    override suspend fun register(
        email: String,
        password: String,
        name: String,
        role: String
    ): OzyuceResult<AuthResult> {
        return withContext(dispatchers.io) {
            // Demo mode - ger?ek API ?a?r?s? yerine mock data
            try {
                kotlinx.coroutines.delay(1000) // Simulated network delay
                
                val mockAuthResult = AuthResult(
                    token = "demo_jwt_token_${System.currentTimeMillis()}",
                    user = User(
                        id = "demo_user_${System.currentTimeMillis()}",
                        name = name,
                        email = email,
                        role = role
                    )
                )
                
                // Save mock auth data
                dataStore.edit { preferences ->
                    preferences[tokenKey] = mockAuthResult.token
                    preferences[userIdKey] = mockAuthResult.user.id
                    preferences[userRoleKey] = mockAuthResult.user.role
                }
                
                OzyuceResult.Success(mockAuthResult)
            } catch (e: Exception) {
                OzyuceResult.Error(e)
            }
        }
    }
    
    override suspend fun logout(): OzyuceResult<Unit> {
        return withContext(dispatchers.io) {
            try {
                // Demo mode - sadece local data temizle
                clearAuthData()
                OzyuceResult.Success(Unit)
            } catch (e: Exception) {
                // Network hatas? olsa bile local data'y? temizle
                clearAuthData()
                OzyuceResult.Success(Unit)
            }
        }
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return withContext(dispatchers.io) {
            dataStore.data.map { preferences ->
                preferences[tokenKey] != null
            }.first()
        }
    }
    
    override suspend fun getCurrentUserId(): String? {
        return withContext(dispatchers.io) {
            dataStore.data.map { preferences ->
                preferences[userIdKey]
            }.first()
        }
    }
    
    override suspend fun getCurrentUserRole(): String? {
        return withContext(dispatchers.io) {
            dataStore.data.map { preferences ->
                preferences[userRoleKey]
            }.first()
        }
    }
    
    private suspend fun saveAuthData(authResponse: com.ozyuce.maps.feature.auth.data.remote.dto.AuthResponseDto) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = authResponse.token
            preferences[userIdKey] = authResponse.user.id
            preferences[userRoleKey] = authResponse.user.role
        }
    }
    
    private suspend fun clearAuthData() {
        dataStore.edit { preferences ->
            preferences.remove(tokenKey)
            preferences.remove(userIdKey)
            preferences.remove(userRoleKey)
        }
    }
}
