# OzyuceMaps — Yamalar (Adım 11: Harita + Canlı Konum/WebSocket + ETA + Reports)

Bu yama **Map** özelliğini, canlı konum akışını (FusedLocation + WebSocket placeholder),
basit bir **ETA** hesabını (stub) ve **Reports** ekranını ekler. Mevcut kodu koru; **yalnız eksikleri** uygula.

> Not: Network uçları prod değilse, WebSocket ve Distance Matrix entegrasyonları **stub** olarak bırakılır.
> API hazır olunca bağlayacağız.

---

## 11.1) Location sağlayıcı ve DI

`app/src/main/java/com/ozyuce/maps/data/location/LocationClient.kt`
```kotlin
package com.ozyuce.maps.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationClient @Inject constructor(
  private val fused: FusedLocationProviderClient,
  private val request: LocationRequest
) {
  @SuppressLint("MissingPermission")
  fun stream(): Flow<Location> = callbackFlow {
    val callback = object : LocationCallback() {
      override fun onLocationResult(result: LocationResult) {
        result.lastLocation?.let { trySend(it).isSuccess }
      }
    }
    fused.requestLocationUpdates(request, callback, null)
    awaitClose { fused.removeLocationUpdates(callback) }
  }
}
```

`app/src/main/java/com/ozyuce/maps/di/LocationModule.kt`
```kotlin
package com.ozyuce.maps.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
  @Provides @Singleton
  fun provideFused(@ApplicationContext ctx: Context): FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(ctx)

  @Provides @Singleton
  fun provideRequest(): LocationRequest =
    LocationRequest.Builder(2000L)
      .setMinUpdateIntervalMillis(1000L)
      .setMinUpdateDistanceMeters(3f)
      .build()
}
```

---

## 11.2) WebSocket (placeholder) — canlı konum gönderimi

`app/src/main/java/com/ozyuce/maps/data/realtime/LocationSocketClient.kt`
```kotlin
package com.ozyuce.maps.data.realtime

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton
import com.ozyuce.maps.BuildConfig

@Singleton
class LocationSocketClient @Inject constructor(
  private val client: OkHttpClient
) {
  private var ws: WebSocket? = null

  fun connect(): Boolean {
    val wsUrl = BuildConfig.BASE_URL.replace("https://", "wss://")
                                   .replace("http://", "ws://")
                                   .trimEnd('/') + "/realtime/locations"
    val req = Request.Builder().url(wsUrl).build()
    ws = client.newWebSocket(req, object : WebSocketListener() {})
    return ws != null
  }

  fun sendJson(json: String) {
    ws?.send(json)
  }

  fun close() { ws?.close(1000, "bye"); ws = null }
}
```

---

## 11.3) Map ViewModel — konum akışı + WS + ETA (stub)

`app/src/main/java/com/ozyuce/maps/feature/map/MapViewModel.kt`
```kotlin
package com.ozyuce.maps.feature.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.data.location.LocationClient
import com.ozyuce.maps.data.realtime.LocationSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.*

data class MapUiState(
  val latitude: Double? = null,
  val longitude: Double? = null,
  val etaMinutes: Int? = null,
  val isStreaming: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor(
  private val locationClient: LocationClient,
  private val socket: LocationSocketClient
) : ViewModel() {

  private val _ui = MutableStateFlow(MapUiState())
  val ui: StateFlow<MapUiState> = _ui

  private var streamJob: Job? = null

  fun toggleStream() {
    if (_ui.value.isStreaming) stopStream() else startStream()
  }

  private fun startStream() {
    if (socket.connect()) {
      _ui.value = _ui.value.copy(isStreaming = true)
      streamJob = viewModelScope.launch {
        locationClient.stream().collectLatest { loc ->
          _ui.value = _ui.value.copy(latitude = loc.latitude, longitude = loc.longitude, etaMinutes = calcEtaStub(loc))
          socket.sendJson("""{"lat":${loc.latitude},"lng":${loc.longitude},"t":${System.currentTimeMillis()}}""")
        }
      }
    }
  }

  private fun stopStream() {
    streamJob?.cancel(); streamJob = null
    socket.close()
    _ui.value = _ui.value.copy(isStreaming = false)
  }

  // Basit ETA stub: hedef varış noktasına düz-çizgi mesafe / 30 km/s hız
  private fun calcEtaStub(loc: Location): Int {
    val targetLat = 41.0082; val targetLng = 28.9784 // İstanbul (örnek)
    val dKm = haversine(loc.latitude, loc.longitude, targetLat, targetLng)
    val hours = dKm / 30.0
    return max(1, (hours * 60).roundToInt())
  }

  private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat/2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon/2).pow(2.0)
    return 2 * R * asin(sqrt(a))
  }

  override fun onCleared() {
    super.onCleared()
    stopStream()
  }
}
```

---

## 11.4) Map UI — Google Maps Compose + izin akışı

`app/src/main/java/com/ozyuce/maps/feature/map/MapScreen.kt`
```kotlin
package com.ozyuce.maps.feature.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.maps.android.compose.*

@Composable
fun MapScreen(nav: NavController, vm: MapViewModel = hiltViewModel()) {
  val ui = vm.ui.collectAsState().value
  val camera = rememberCameraPositionState()
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    onResult = { /* no-op, kullanıcı tekrar deneyebilir */ }
  )

  Scaffold(
    topBar = { SmallTopAppBar(title = { Text("Harita") }) },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        text = { Text(if (ui.isStreaming) "Yayın Durdur" else "Yayın Başlat") },
        onClick = { vm.toggleStream() }
      )
    }
  ) { pad ->
    Column(Modifier.fillMaxSize().padding(pad)) {
      GoogleMap(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        cameraPositionState = camera,
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        properties = MapProperties(isMyLocationEnabled = ui.latitude != null)
      ) {
        if (ui.latitude != null && ui.longitude != null) {
          val pos = LatLng(ui.latitude, ui.longitude)
          Marker(state = MarkerState(position = pos), title = "Sürücü")
          LaunchedEffect(pos) { camera.animate(CameraUpdateFactory.newLatLngZoom(pos, 15f)) }
        }
      }

      Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = {
          launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }) { Text("Konum İzni") }

        OutlinedButton(onClick = { vm.toggleStream() }) {
          Text(if (ui.isStreaming) "Durdur" else "Başlat")
        }

        Spacer(Modifier.weight(1f))
        Text("ETA: " + (ui.etaMinutes?.let { "$it dk" } ?: "—"), style = MaterialTheme.typography.titleMedium)
      }
    }
  }
}
```

---

## 11.5) NavGraph — Map ve Reports ekranlarını bağla

`app/src/main/java/com/ozyuce/maps/navigation/AppNavGraph.kt` içine (yoksa ekle/değiştir):
```kotlin
composable(Dest.Map.route) { MapScreen(navController) }
composable(Dest.Reports.route) { ReportsScreen(navController) }
```

---

## 11.6) Reports — basit günlük rapor ekranı (placeholder)

`app/src/main/java/com/ozyuce/maps/feature/reports/ReportsViewModel.kt`
```kotlin
package com.ozyuce.maps.feature.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.domain.repository.DailyReport
import com.ozyuce.maps.domain.usecase.GetDailyReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReportsUiState(
  val report: DailyReport? = null,
  val isLoading: Boolean = false
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
  private val getDailyReport: GetDailyReportUseCase
) : ViewModel() {
  private val _ui = MutableStateFlow(ReportsUiState())
  val ui: StateFlow<ReportsUiState> = _ui

  fun load() {
    viewModelScope.launch {
      _ui.value = _ui.value.copy(isLoading = true)
      when (val r = getDailyReport()) {
        is Result.Success -> _ui.value = ReportsUiState(report = r.data, isLoading = false)
        is Result.Error -> _ui.value = ReportsUiState(report = null, isLoading = false)
        is Result.Loading -> {}
      }
    }
  }
}
```

`app/src/main/java/com/ozyuce/maps/feature/reports/ReportsScreen.kt`
```kotlin
package com.ozyuce.maps.feature.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun ReportsScreen(nav: NavController, vm: ReportsViewModel = hiltViewModel()) {
  val ui = vm.ui.collectAsState().value
  LaunchedEffect(Unit) { vm.load() }

  Scaffold(topBar = { SmallTopAppBar(title = { Text("Günlük Rapor") }) }) { pad ->
    Column(Modifier.padding(pad).padding(16.dp).fillMaxSize()) {
      if (ui.isLoading) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
      Spacer(Modifier.height(12.dp))
      ui.report?.let { r ->
        Text("Tarih: ${r.date}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("Toplam Sefer: ${r.totalRides}")
        Text("Zamanında Oran: ${"%.1f".format(r.onTimeRate * 100)}%")
      } ?: Text("Rapor bulunamadı", style = MaterialTheme.typTypography.bodyLarge)
    }
  }
}
```

---

## 11.7) Manifest hatırlatmaları (zaten varsa atla)
- Konum izinleri (FINE/COARSE) ve `FOREGROUND_SERVICE_LOCATION` daha önce eklendi.
- Maps için API key kullanıyorsan, `meta-data` ekini unutma (proje zaten kullanıyorsa atla):
```xml
<meta-data android:name="com.google.android.geo.API_KEY" android:value="@string/google_maps_key"/>
```

---

## Kabul Ölçütleri
- `./gradlew :app:assembleDevDebug` ✓
- Map ekranında konum izni verildikten sonra kendi konumun marker olarak görünür; FAB ile canlı akış başlat/durdur.
- Akış açıkken, WS istemcisi bağlantı kurar ve her konumda JSON gönderir (stub endpoint).
- ETA alanı stub formülüyle güncellenir.
- Reports ekranı `GetDailyReportUseCase` üzerinden temel metrikleri gösterir.

## Notlar
- Gerçek Distance Matrix entegrasyonunda API anahtarı ve kota yönetimini ekleyeceğiz.
- WebSocket şeması netleştiğinde `LocationSocketClient`'a auth header ve yeniden bağlanma stratejisi eklenecek.
