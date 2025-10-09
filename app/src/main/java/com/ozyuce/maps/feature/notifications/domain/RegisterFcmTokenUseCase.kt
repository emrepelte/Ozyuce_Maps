package com.ozyuce.maps.feature.notifications.domain

import com.ozyuce.maps.core.common.result.OzyuceResult
import javax.inject.Inject

/**
 * FCM token kaydetme use case
 */
class RegisterFcmTokenUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {
    suspend operator fun invoke(token: String): OzyuceResult<Unit> {
        if (token.isBlank()) {
            return OzyuceResult.Error(IllegalArgumentException("FCM token bo? olamaz"))
        }
        
        return notificationsRepository.registerFcmToken(token)
    }
}
