package com.ozyuce.maps.feature.map.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.feature.map.domain.model.RoutePolyline
import com.ozyuce.maps.feature.map.domain.model.StopMarker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Rota detaylar?n? (polyline ve duraklar) getiren use case
 */
class GetRouteDetailsUseCase @Inject constructor(
    private val mapRepository: MapRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(routeId: String): Result<Pair<RoutePolyline, List<StopMarker>>> {
        return withContext(dispatcherProvider.io) {
            try {
                // Rota ?izgisi
                val polylineResult = mapRepository.getRoutePolyline(routeId)
                if (polylineResult !is Result.Success) {
                    return@withContext polylineResult as Result<Pair<RoutePolyline, List<StopMarker>>>
                }

                // Durak marker'lar?
                val markersResult = mapRepository.getStopMarkers(routeId)
                if (markersResult !is Result.Success) {
                    return@withContext markersResult as Result<Pair<RoutePolyline, List<StopMarker>>>
                }

                Result.Success(Pair(polylineResult.data, markersResult.data))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    fun getStopMarkersFlow(routeId: String): Flow<List<StopMarker>> {
        return mapRepository.getStopMarkersFlow(routeId)
            .flowOn(dispatcherProvider.io)
    }
}
