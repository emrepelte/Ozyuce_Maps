package com.ozyuce.maps.feature.auth.domain

import com.ozyuce.maps.core.common.Constants
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.auth.domain.model.UserRole
import javax.inject.Inject

/**
 * Kullan?c? kay?t use case
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String, 
        password: String, 
        name: String, 
        role: String
    ): OzyuceResult<AuthResult> {
        
        // Name validation
        if (name.isBlank() || name.length < 2) {
            return OzyuceResult.Error(IllegalArgumentException("?sim en az 2 karakter olmal?d?r"))
        }
        
        // Email validation
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return OzyuceResult.Error(IllegalArgumentException("Ge?erli bir email adresi girin"))
        }
        
        // Password validation
        if (password.isBlank() || password.length < 6) {
            return OzyuceResult.Error(IllegalArgumentException("?ifre en az 6 karakter olmal?d?r"))
        }
        
        // Role validation
        if (role !in listOf(UserRole.DRIVER.name, UserRole.CUSTOMER.name)) {
            return OzyuceResult.Error(IllegalArgumentException("Ge?ersiz kullan?c? rol?"))
        }
        
        return authRepository.register(email, password, name, role)
    }
}
