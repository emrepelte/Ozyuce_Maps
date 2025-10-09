package com.ozyuce.maps.feature.map.domain

import com.ozyuce.maps.core.common.DispatcherProvider
import com.ozyuce.maps.core.common.result.OzyuceResult
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
    suspend operator fun invoke(routeId: String): OzyuceResult<Pair<RoutePolyline, List<StopMarker>>> =
        withContext(dispatcherProvider.io) {
            try {
                val polyline = when (val polylineResult = mapRepository.getRoutePolyline(routeId)) {
                    is OzyuceResult.Success -> polylineResult.data
                    is OzyuceResult.Error -> return@withContext OzyuceResult.Error(polylineResult.exception)
                    OzyuceResult.Loading -> return@withContext OzyuceResult.Loading
                }

                val markers = when (val markersResult = mapRepository.getStopMarkers(routeId)) {
                    is OzyuceResult.Success -> markersResult.data
                    is OzyuceResult.Error -> return@withContext OzyuceResult.Error(markersResult.exception)
                    OzyuceResult.Loading -> return@withContext OzyuceResult.Loading
                }

                OzyuceResult.Success(polyline to markers)
            } catch (e: Exception) {
                OzyuceResult.Error(e)
            }
        }

    fun getStopMarkersFlow(routeId: String): Flow<List<StopMarker>> {
        return mapRepository.getStopMarkersFlow(routeId)
            .flowOn(dispatcherProvider.io)
    }
}
