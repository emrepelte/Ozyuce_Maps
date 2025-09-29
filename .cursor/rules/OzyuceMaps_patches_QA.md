
# OzyuceMaps — Mini Patch (QA & Sertleştirme)

Bu mini yama; **izin akışları**, **Map güvenlik kontrolleri**, **WebSocket yeniden bağlanma/backoff** ve
küçük DX iyileştirmeleri içerir. Mevcut kodu koru; **sadece eksikleri** uygula.

> Not: Aşağıdaki kodlar; senin son değişikliklerinle uyumlu olacak biçimde **var olan dosyaları genişletir**.
> Dosya isimleri/proje yapın farklıysa ilgili yerlere taşı. Kod bloklarını **parça parça** uygula.

---

## 1) Map güvenlik & izin guard’ları

### 1.1) Basit izin yardımcıları
`app/src/main/java/com/ozyuce/maps/util/Permissions.kt` (yoksa oluştur)
```kotlin
package com.ozyuce.maps.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

fun hasFineLocation(ctx: Context) =
  ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun hasCoarseLocation(ctx: Context) =
  ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun hasNotifications(ctx: Context): Boolean =
  if (Build.VERSION.SDK_INT < 33) true
  else ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
```

### 1.2) MapScreen’de SecurityException koruması
`app/src/main/java/com/ozyuce/maps/feature/map/MapScreen.kt` içinde GoogleMap çağrısını **şöyle** şartla:
```kotlin
import androidx.compose.ui.platform.LocalContext
import com.ozyuce.maps.util.hasFineLocation
import com.ozyuce.maps.util.hasCoarseLocation

// ...
val ctx = LocalContext.current
val hasLoc = hasFineLocation(ctx) || hasCoarseLocation(ctx)

GoogleMap(
  modifier = Modifier.weight(1f).fillMaxWidth(),
  cameraPositionState = camera,
  uiSettings = MapUiSettings(zoomControlsEnabled = false),
  properties = MapProperties(isMyLocationEnabled = hasLoc && ui.latitude != null)
) {
  // ...
}
```
> Böylece izin yokken `isMyLocationEnabled` **false** kalır, `SecurityException` alınmaz.

### 1.3) Konum ayarları (GPS) çözümü için yardımcı
`app/src/main/java/com/ozyuce/maps/util/LocationSettings.kt` (yoksa ekle)
```kotlin
package com.ozyuce.maps.util

import android.app.Activity
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

fun buildSettingsRequest(request: LocationRequest): LocationSettingsRequest =
  LocationSettingsRequest.Builder().addLocationRequest(request).setAlwaysShow(true).build()

suspend fun tryResolveLocationSettings(activity: Activity, request: LocationRequest, launcher: (IntentSender) -> Unit) {
  val client = LocationServices.getSettingsClient(activity)
  val task = client.checkLocationSettings(buildSettingsRequest(request))
  try {
    val result = task.await() // eğer 'await' extension yoksa standart onSuccess/onFailure ile uygula
  } catch (e: Exception) {
    val resolvable = (e as? ResolvableApiException) ?: return
    try { launcher(resolvable.resolution.intentSender) } catch (_: IntentSender.SendIntentException) {}
  }
}
```
> İstersen `rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult())` ile çağır.

---

## 2) WebSocket dayanıklılığı (exponential backoff + ping)

### 2.1) OkHttp ping aralığı (yoksa ekle)
**OkHttpClient** üretildiği yerde (genelde `core/network/.../NetworkModule.kt`) şu satırı builder’a **ekle**:
```kotlin
.pingInterval(20, java.util.concurrent.TimeUnit.SECONDS)
```
> Zaten varsa tekrarlama.

### 2.2) Backoff’lu yeniden bağlanma
`app/src/main/java/com/ozyuce/maps/core/network/websocket/LocationWebSocketClient.kt` (senin dosya yoluna göre)
aşağıdaki **yardımcı alanları ve dinleyiciyi** ekle/uyarla:

```kotlin
// Üst kısım:
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

private val WS_SCOPE = CoroutineScope(Dispatchers.IO)
private var reconnectJob: Job? = null
private var reconnectAttempts = 0
private val shouldReconnect = AtomicBoolean(false)

private fun scheduleReconnect(connectBlock: () -> Unit) {
  if (!shouldReconnect.get()) return
  reconnectJob?.cancel()
  val delayMs = (1000L * (1 shl reconnectAttempts)).coerceAtMost(30_000L) // 1s,2s,4s..30s
  reconnectJob = WS_SCOPE.launch {
    delay(delayMs)
    reconnectAttempts = min(reconnectAttempts + 1, 5)
    connectBlock()
  }
}
```

WebSocketListener içinde `onFailure`/`onClosed` olaylarında:
```kotlin
override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
  if (shouldReconnect.get()) scheduleReconnect { connect() }
}

override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
  if (shouldReconnect.get()) scheduleReconnect { connect() }
}
```

`connect()` başında ve `close()` içinde:
```kotlin
fun connect(): Boolean {
  shouldReconnect.set(true)
  reconnectAttempts = 0
  // ... mevcut connect mantığın
}

fun close() {
  shouldReconnect.set(false)
  reconnectJob?.cancel(); reconnectJob = null
  // ... mevcut kapatma mantığın
}
```

> Böylece bağlantı koptuğunda **otomatik** geri bağlanır; `close()` çağrılınca durur.

---

## 3) Bildirim izni (Android 13+) için nazik hatırlatma (opsiyonel)

`app/src/main/java/com/ozyuce/maps/feature/common/NotificationPermissionPrompt.kt`
```kotlin
package com.ozyuce.maps.feature.common

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.ozyuce.maps.util.hasNotifications

@Composable
fun NotificationPermissionPrompt(modifier: Modifier = Modifier) {
  if (Build.VERSION.SDK_INT < 33) return
  val ctx = LocalContext.current
  if (hasNotifications(ctx)) return

  val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }
  ElevatedCard(modifier) {
    ListItem(
      headlineContent = { Text("Bildirim izni") },
      supportingContent = { Text("Servis uyarıları ve mesajlar için bildirime izin ver.") },
      trailingContent = { Button(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) { Text("İzin ver") } }
    )
  }
}
```
> Bu kartı uygun bir ana ekrana/ayar sayfasına koyabilirsin.

---

## 4) Küçük DX/QA iyileştirmeleri

- **MapScreen**: FAB durumunda (başlat/durdur) `SnackbarHostState` üzerinden kısa bir onay mesajı göster.
- **ServiceScreen**: `onStart/onEnd` çağrıları sırasında butonları disable etme mantığı zaten var; hata mesajında 
  throwable mesajını güvenli biçimde kısalt (`take(120)`).
- **StopsScreen**: `stopId` boşsa (personelin stopId yoksa) check/uncheck butonlarını **disable** yap.

```kotlin
// StopsScreen.kt içinde:
val canCheck = !p.stopId.isNullOrBlank()
IconButton(onClick = { vm.toggleCheck(p.stopId ?: "", true) }, enabled = canCheck) { /* ... */ }
IconButton(onClick = { vm.toggleCheck(p.stopId ?: "", false) }, enabled = canCheck) { /* ... */ }
```

---

## 5) Kabul ölçütleri
- Derleme: `./gradlew :app:assembleDevDebug` ✓
- İzin yokken Map açıldığında **crash yok**, myLocation kapalı, kullanıcı izni isteyebiliyor.
- WS koparsa 1s→2s→4s… **otomatik** yeniden bağlanır; manuel `close()` durdurur.
- Stops ekranında geçersiz `stopId` için butonlar devre dışı.
- (Opsiyonel) Bildirim izni istemi 33+ cihazlarda görünüyor.

