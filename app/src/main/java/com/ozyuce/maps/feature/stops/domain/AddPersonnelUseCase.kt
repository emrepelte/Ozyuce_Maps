package com.ozyuce.maps.feature.stops.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import com.ozyuce.maps.feature.stops.domain.model.AddPersonnelRequest
import kotlinx.coroutines.withContext
import com.ozyuce.maps.feature.stops.domain.validation.PhoneNumberValidator
import javax.inject.Inject

/**
 * Personel ekleme use case
 */
class AddPersonnelUseCase @Inject constructor(
    private val stopsRepository: StopsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(
        name: String, 
        surname: String, 
        stopId: String,
        phoneNumber: String? = null
    ): OzyuceResult<Personnel> {
        return withContext(dispatcherProvider.io) {
            // Validation
            if (name.isBlank() || name.length < 2) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("İsim en az 2 karakter olmalıdır"))
            }
            
            if (surname.isBlank() || surname.length < 2) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Soyisim en az 2 karakter olmalıdır"))
            }
            
            if (stopId.isBlank()) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Durak seçilmelidir"))
            }

            if (phoneNumber != null && !PhoneNumberValidator.validate(phoneNumber)) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Geçersiz telefon numarası formatı"))
            }
            
            val formattedPhoneNumber = phoneNumber?.let { PhoneNumberValidator.format(it) }
            
            val request = AddPersonnelRequest(
                name = name.trim(),
                surname = surname.trim(),
                phoneNumber = formattedPhoneNumber,
                stopId = stopId
            )
            
            stopsRepository.addPersonnel(request)
        }
    }
}
