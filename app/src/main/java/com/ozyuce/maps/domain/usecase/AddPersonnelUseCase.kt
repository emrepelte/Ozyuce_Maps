package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.Personnel
import com.ozyuce.maps.domain.repository.PersonnelRepository
import javax.inject.Inject

class AddPersonnelUseCase @Inject constructor(
    private val repo: PersonnelRepository
) {
    suspend operator fun invoke(personnel: Personnel): AppResult<Unit> =
        repo.addPersonnel(personnel)
}