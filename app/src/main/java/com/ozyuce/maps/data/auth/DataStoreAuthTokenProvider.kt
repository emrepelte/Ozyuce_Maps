package com.ozyuce.maps.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ozyuce.maps.core.common.Constants
import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class DataStoreAuthTokenProvider @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthTokenProvider {

    private val tokenKey = stringPreferencesKey(Constants.TOKEN_KEY)

    override suspend fun getToken(): String? {
        return dataStore.data.map { prefs -> prefs[tokenKey] }.first()
    }

    override suspend fun setToken(token: String?) {
        dataStore.edit { prefs ->
            if (token == null) {
                prefs.remove(tokenKey)
            } else {
                prefs[tokenKey] = token
            }
        }
    }
}