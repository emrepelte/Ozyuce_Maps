package com.ozyuce.maps.core.common.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.ozyuce.maps.core.common.result.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : NotificationRepository {

    override suspend fun updateToken(token: String): Result<Unit> {
        return try {
            // TODO: Token'i sunucuya gonder
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error(e)
        }
    }

    override fun getToken(): Flow<Result<String?>> = callbackFlow {
        trySend(Result.loading())

        try {
            val token = firebaseMessaging.token.await()
            trySend(Result.success(token))
        } catch (e: Exception) {
            trySend(Result.error(e))
        }

        awaitClose()
    }
}


