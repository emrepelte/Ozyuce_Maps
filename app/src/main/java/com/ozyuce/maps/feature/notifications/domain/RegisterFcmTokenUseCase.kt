package com.ozyuce.maps.feature.notifications.domain

import com.ozyuce.maps.core.common.result.Result
import javax.inject.Inject

/**
 * FCM token kaydetme use case
 */
class RegisterFcmTokenUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        if (token.isBlank()) {
            return Result.Error(IllegalArgumentException("FCM token bo? olamaz"))
        }
        
        return notificationsRepository.registerFcmToken(token)
    }
}
