package com.ozyuce.maps.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ozyuce.maps.core.common.Constants
import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.NotificationsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.withContext

@Singleton
class DefaultNotificationsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dispatcherProvider: DispatcherProvider
) : NotificationsRepository {

    private val tokenKey = stringPreferencesKey(Constants.FCM_TOKEN_KEY)

    override suspend fun saveFcmToken(token: String): AppResult<Unit> = runCatching {
        withContext(dispatcherProvider.io) {
            dataStore.edit { prefs -> prefs[tokenKey] = token }
            Unit
        }
    }.toAppResult()
}

private fun <T> Result<T>.toAppResult(): AppResult<T> = fold(
    onSuccess = { AppResult.success(it) },
    onFailure = { AppResult.error(it) }
)