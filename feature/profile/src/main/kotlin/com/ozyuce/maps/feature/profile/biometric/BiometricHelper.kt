package com.ozyuce.maps.feature.profile.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricHelper @Inject constructor() {

    fun canAuthenticate(context: Context): Boolean {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return BiometricManager.from(context).canAuthenticate(authenticators) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun createPrompt(activity: FragmentActivity): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Timber.d("Auth succeeded: $result")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Timber.e("Auth error($errorCode): $errString")
            }

            override fun onAuthenticationFailed() {
                Timber.w("Auth failed")
            }
        }
        return BiometricPrompt(activity, executor, callback)
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String = "Giriş yap",
        subtitle: String = "Biyometrik doğrulama"
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        createPrompt(activity).authenticate(promptInfo)
    }
}