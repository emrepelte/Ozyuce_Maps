package com.ozyuce.maps.core.network.websocket

import com.ozyuce.maps.BuildConfig
import com.ozyuce.maps.core.common.Constants
import com.ozyuce.maps.feature.map.domain.model.VehicleLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.min
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null
    private val messageChannel = Channel<String>(Channel.BUFFERED)
    private val locationChannel = Channel<VehicleLocation>(Channel.BUFFERED)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var reconnectJob: Job? = null
    private var shouldReconnect = false
    private var reconnectAttempts = 0
    private var lastUrl: String? = null

    fun connect(url: String? = null) {
        shouldReconnect = true
        val targetUrl = url ?: buildWebSocketUrl()
        lastUrl = targetUrl
        cancelReconnect()

        val request = Request.Builder()
            .url(targetUrl)
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Timber.d("WebSocket opened")
                reconnectAttempts = 0
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                messageChannel.trySend(text)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Timber.d("WebSocket closed: $code - $reason")
                this@LocationWebSocketClient.webSocket = null
                if (shouldReconnect) scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Timber.e(t, "WebSocket failure: ${response?.message}")
                this@LocationWebSocketClient.webSocket = null
                if (shouldReconnect) scheduleReconnect()
            }
        })
    }

    fun disconnect() {
        shouldReconnect = false
        cancelReconnect()
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        messageChannel.close()
        locationChannel.close()
    }

    fun sendLocation(location: VehicleLocation) {
        webSocket?.send(location.toJson())
    }

    fun getMessageFlow(): Flow<String> = messageChannel.receiveAsFlow()

    fun getLocationFlow(): Flow<VehicleLocation> = locationChannel.receiveAsFlow()

    private fun scheduleReconnect() {
        val url = lastUrl ?: return
        val delayMillis = calculateBackoffMillis()
        Timber.d("Scheduling WebSocket reconnect in ${delayMillis}ms (attempt ${reconnectAttempts + 1})")
        reconnectJob = scope.launch {
            delay(delayMillis)
            if (shouldReconnect) {
                connect(url)
            }
        }
        if (reconnectAttempts < MAX_BACKOFF_STEPS) {
            reconnectAttempts += 1
        }
    }

    private fun cancelReconnect() {
        reconnectJob?.cancel()
        reconnectJob = null
    }

    private fun calculateBackoffMillis(): Long {
        val seconds = min(30, 1 shl reconnectAttempts)
        return seconds * 1000L
    }

    private fun buildWebSocketUrl(): String {
        val base = (BuildConfig.BASE_URL.ifBlank { Constants.WEBSOCKET_URL })
            .replace("https://", "wss://")
            .replace("http://", "ws://")
            .trimEnd('/')
        return "$base/realtime/locations"
    }

    private fun VehicleLocation.toJson(): String {
        return """
            {
                "id": "$id",
                "routeId": "$routeId",
                "driverId": "$driverId",
                "location": {
                    "lat": ${location.latitude},
                    "lng": ${location.longitude}
                },
                "heading": $heading,
                "speed": $speed,
                "timestamp": ${timestamp.time},
                "isActive": $isActive
            }
        """.trimIndent()
    }

    private companion object {
        private const val MAX_BACKOFF_STEPS = 5 // 1, 2, 4, 8, 16, 32 (capped to 30s)
    }
}
