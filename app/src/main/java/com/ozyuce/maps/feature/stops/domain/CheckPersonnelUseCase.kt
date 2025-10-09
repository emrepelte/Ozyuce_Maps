package com.ozyuce.maps.feature.stops.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.feature.stops.domain.model.Personnel
import com.ozyuce.maps.feature.stops.domain.model.PersonnelCheck
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

/**
 * Personeli i?aretleme i?lemini y?neten Use Case.
 */
class CheckPersonnelUseCase @Inject constructor(
    private val stopsRepository: StopsRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(
        personnelId: String, 
        stopId: String, 
        isChecked: Boolean,
        checkedBy: String,
        notes: String? = null
    ): OzyuceResult<Personnel> {
        return withContext(dispatcherProvider.io) {
            // Validation
            if (personnelId.isBlank() || stopId.isBlank() || checkedBy.isBlank()) {
                return@withContext OzyuceResult.Error(IllegalArgumentException("Gerekli alanlar bo? olamaz"))
            }
            
            val personnelCheck = PersonnelCheck(
                personnelId = personnelId,
                stopId = stopId,
                isChecked = isChecked,
                checkTime = Date(),
                checkedBy = checkedBy,
                notes = notes
            )
            
            stopsRepository.checkPersonnel(personnelCheck)
        }
    }
}
