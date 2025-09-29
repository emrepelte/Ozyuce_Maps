package com.ozyuce.maps.feature.auth.domain

import com.ozyuce.maps.core.common.result.Result
import javax.inject.Inject

/**
 * Kullan?c? giri? use case
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResult> {
        // Email validation
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error(IllegalArgumentException("Ge?erli bir email adresi girin"))
        }
        
        // Password validation
        if (password.isBlank() || password.length < 6) {
            return Result.Error(IllegalArgumentException("?ifre en az 6 karakter olmal?d?r"))
        }
        
        return authRepository.login(email, password)
    }
}
