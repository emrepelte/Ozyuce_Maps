package com.ozyuce.maps.feature.profile.domain

import com.ozyuce.maps.core.common.result.Result

/**
 * Profil modülü, oturum kapatma işlemini bu arayüz üzerinden talep eder.
 * Gerçek implementasyon uygulama katmanında sağlanır.
 */
fun interface LogoutUseCase {
    suspend operator fun invoke(): Result<Unit>
}

