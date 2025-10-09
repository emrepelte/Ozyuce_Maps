package com.ozyuce.maps.feature.service.data.repository

import com.ozyuce.maps.core.common.result.OzyuceResult
import com.ozyuce.maps.core.database.dao.ServiceSessionDao
import com.ozyuce.maps.core.database.entity.ServiceSessionEntity
import com.ozyuce.maps.feature.service.domain.ServiceRepository
import com.ozyuce.maps.feature.service.domain.model.Route
import com.ozyuce.maps.feature.service.domain.model.ServiceSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * ServiceRepository implementasyonu - Demo mode
 */
@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val serviceSessionDao: ServiceSessionDao
) : ServiceRepository {

    override suspend fun startService(routeId: String): OzyuceResult<ServiceSession> {
        return try {
            // Demo mode - ger?ek API ?a?r?s? yerine mock data
            kotlinx.coroutines.delay(1000) // Network simulation
            
            val sessionId = "session_${System.currentTimeMillis()}"
            val mockSession = ServiceSession(
                id = sessionId,
                routeId = routeId,
                driverId = "demo_driver_id",
                startTime = Date(),
                endTime = null,
                isActive = true,
                totalDistance = 0.0,
                averageSpeed = 0.0,
                fuelConsumption = null,
                stopsCompleted = 0,
                totalStops = getMockRoute(routeId).totalStops
            )
            
            // Room'a kaydet
            val entity = ServiceSessionEntity(
                id = mockSession.id,
                routeId = mockSession.routeId,
                driverId = mockSession.driverId,
                startTime = mockSession.startTime,
                endTime = mockSession.endTime,
                isActive = mockSession.isActive
            )
            serviceSessionDao.insertSession(entity)
            
            OzyuceResult.Success(mockSession)
        } catch (e: Exception) {
            OzyuceResult.Error(e)
        }
    }

    override suspend fun endService(sessionId: String): OzyuceResult<ServiceSession> {
        return try {
            kotlinx.coroutines.delay(800) // Network simulation
            
            val endTime = Date()
            serviceSessionDao.endSession(sessionId, endTime)
            
            // Mock ended session
            val endedSession = getCurrentSession().let { result ->
                when (result) {
                    is OzyuceResult.Success -> result.data?.copy(
                        endTime = endTime,
                        isActive = false,
                        totalDistance = Random.nextDouble(15.0, 50.0), // Mock distance
                        averageSpeed = Random.nextDouble(25.0, 45.0),  // Mock speed
                        stopsCompleted = Random.nextInt(8, 12),            // Mock completed stops
                        fuelConsumption = Random.nextDouble(3.5, 8.2)         // Mock fuel
                    )
                    else -> null
                }
            }
            
            if (endedSession != null) {
                OzyuceResult.Success(endedSession)
            } else {
                OzyuceResult.Error(Exception("Servis bitirilemedi"))
            }
        } catch (e: Exception) {
            OzyuceResult.Error(e)
        }
    }

    override suspend fun getCurrentSession(): OzyuceResult<ServiceSession?> {
        return try {
            val entity = serviceSessionDao.getCurrentSession()
            val session = entity?.let { mapEntityToDomain(it) }
            OzyuceResult.Success(session)
        } catch (e: Exception) {
            OzyuceResult.Error(e)
        }
    }

    override fun getCurrentSessionFlow(): Flow<ServiceSession?> {
        return serviceSessionDao.getCurrentSessionFlow().map { entity ->
            entity?.let { mapEntityToDomain(it) }
        }
    }

    override suspend fun getAvailableRoutes(): OzyuceResult<List<Route>> {
        return try {
            kotlinx.coroutines.delay(500) // Network simulation
            
            // Demo rotalar
            val mockRoutes = listOf(
                Route(
                    id = "route_1",
                    name = "Ana Kamp?s Rotas?",
                    description = "Merkez-Kamp?s-Yurtlar-?ehir Merkezi",
                    totalStops = 12,
                    estimatedDuration = 45,
                    estimatedDistance = 18.5
                ),
                Route(
                    id = "route_2", 
                    name = "?ehir ??i Rotas?",
                    description = "?l?eler aras? ba?lant? rotas?",
                    totalStops = 8,
                    estimatedDuration = 30,
                    estimatedDistance = 12.3
                ),
                Route(
                    id = "route_3",
                    name = "Sanayi Rotas?", 
                    description = "OSB-Fabrikalar-?? Merkezi",
                    totalStops = 10,
                    estimatedDuration = 35,
                    estimatedDistance = 15.7
                )
            )
            
            OzyuceResult.Success(mockRoutes)
        } catch (e: Exception) {
            OzyuceResult.Error(e)
        }
    }

    override suspend fun getServiceHistory(): OzyuceResult<List<ServiceSession>> {
        return try {
            val entities = serviceSessionDao.getSessionHistory("demo_driver_id")
            val sessions = entities.map { mapEntityToDomain(it) }
            OzyuceResult.Success(sessions)
        } catch (e: Exception) {
            OzyuceResult.Error(e)
        }
    }

    private fun mapEntityToDomain(entity: ServiceSessionEntity): ServiceSession {
        return ServiceSession(
            id = entity.id,
            routeId = entity.routeId,
            driverId = entity.driverId,
            startTime = entity.startTime,
            endTime = entity.endTime,
            isActive = entity.isActive,
            totalDistance = 0.0, // Room entity'den gelmiyor, mock data
            averageSpeed = 0.0,
            fuelConsumption = null,
            stopsCompleted = 0,
            totalStops = getMockRoute(entity.routeId).totalStops
        )
    }
    
    private fun getMockRoute(routeId: String): Route {
        return when (routeId) {
            "route_1" -> Route("route_1", "Ana Kamp?s", "", 12, 45, 18.5)
            "route_2" -> Route("route_2", "?ehir ??i", "", 8, 30, 12.3)
            "route_3" -> Route("route_3", "Sanayi", "", 10, 35, 15.7)
            else -> Route("default", "Varsay?lan Rota", "", 10, 40, 15.0)
        }
    }
}
