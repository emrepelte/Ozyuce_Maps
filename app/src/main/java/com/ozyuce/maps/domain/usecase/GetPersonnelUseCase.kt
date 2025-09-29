package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.domain.repository.Personnel
import com.ozyuce.maps.domain.repository.PersonnelRepository
import javax.inject.Inject

class GetPersonnelUseCase @Inject constructor(
    private val repo: PersonnelRepository
) {
    suspend operator fun invoke(routeId: String): AppResult<List<Personnel>> = repo.getPersonnel(routeId)
}