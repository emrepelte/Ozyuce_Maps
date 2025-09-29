
# OzyuceMaps — Yamalar (Adım 8–10: Auth, Service Başlat/Bitir, Stops+Personnel — VM + UI + Flow)

Bu yama **UI + ViewModel** katmanlarını ekler ve mevcut domain/use‑case’lerle kablolama yapar.
Mevcut kodu koru; dosya varsa **yalnız eksikleri** uygula. Feature'lar ayrı modüllerdeyse
dosya yollarını ilgili modülün `src/main/java` altına taşı (aksi halde `app/` altında kullan).

---

## Ortak: UiEvent toplama yardımcıları (opsiyonel)
`app/src/main/java/com/ozyuce/maps/ui/EventCollectors.kt`
```kotlin
package com.ozyuce.maps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T> CollectLatest(flow: Flow<T>, onEvent: (T) -> Unit) {
  LaunchedEffect(Unit) { flow.collectLatest(onEvent) }
}
```

---

## 8) Auth — Login ekranı ve ViewModel

### 8.1) ViewModel
`app/src/main/java/com/ozyuce/maps/feature/auth/AuthViewModel.kt`
```kotlin
package com.ozyuce.maps.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.ui.UiEvent
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
  val email: String = "",
  val password: String = "",
  val isLoading: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
  private val login: LoginUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(AuthUiState())
  val uiState = _uiState.asStateFlow()

  private val _events = MutableSharedFlow<UiEvent>()
  val events = _events.asSharedFlow()

  fun onEmailChange(v: String) = _uiState.update { it.copy(email = v) }
  fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v) }

  fun onLoginClick() {
    val (email, password) = _uiState.value.let { it.email to it.password }
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      when (val res = login(email, password)) {
        is Result.Success -> _events.emit(UiEvent.Navigate("dashboard"))
        is Result.Error -> _events.emit(UiEvent.ShowSnackbar(res.throwable.message ?: "Giriş başarısız"))
        is Result.Loading -> { /* yoksay */ }
      }
      _uiState.update { it.copy(isLoading = false) }
    }
  }
}
```

### 8.2) UI — LoginScreen
`app/src/main/java/com/ozyuce/maps/feature/auth/LoginScreen.kt`
```kotlin
package com.ozyuce.maps.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ozyuce.maps.core.common.ui.UiEvent
import com.ozyuce.maps.navigation.Dest
import com.ozyuce.maps.ui.CollectLatest

@Composable
fun LoginScreen(nav: NavController, vm: AuthViewModel = hiltViewModel()) {
  val ui = vm.uiState.collectAsState().value
  val snackbar = remember { SnackbarHostState() }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbar) }
  ) { pad ->
    Column(Modifier.padding(pad).padding(16.dp).fillMaxSize()) {
      Text("Giriş Yap", style = MaterialTheme.typography.headlineSmall)
      Spacer(Modifier.height(16.dp))

      OutlinedTextField(
        value = ui.email, onValueChange = vm::onEmailChange,
        label = { Text("Email") }, singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(Modifier.height(8.dp))
      OutlinedTextField(
        value = ui.password, onValueChange = vm::onPasswordChange,
        label = { Text("Şifre") }, singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
      )
      Spacer(Modifier.height(16.dp))

      Button(
        onClick = vm::onLoginClick,
        enabled = !ui.isLoading,
        modifier = Modifier.fillMaxWidth().height(48.dp)
      ) {
        if (ui.isLoading) CircularProgressIndicator(strokeWidth = 2.dp)
        else Text("Giriş")
      }
    }
  }

  CollectLatest(vm.events) { e ->
    when (e) {
      is UiEvent.Navigate -> nav.navigate(Dest.Dashboard.route) {
        popUpTo(Dest.Login.route) { inclusive = true }
      }
      is UiEvent.ShowSnackbar -> { LaunchedEffect(e.message) { snackbar.showSnackbar(e.message) } }
    }
  }
}
```

### 8.3) NavGraph entegrasyonu (placeholder yerine gerçek ekran)
`app/src/main/java/com/ozyuce/maps/navigation/AppNavGraph.kt` içinde **Login** rotasını şuna çevir (yoksa ekle):
```kotlin
composable(Dest.Login.route) { LoginScreen(navController) }
```

---

## 9) Service — Başlat/Bitir

### 9.1) ViewModel
`app/src/main/java/com/ozyuce/maps/feature/service/ServiceViewModel.kt`
```kotlin
package com.ozyuce.maps.feature.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.ui.UiEvent
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.domain.usecase.EndServiceUseCase
import com.ozyuce.maps.domain.usecase.StartServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ServiceUiState(
  val isActive: Boolean = false,
  val isLoading: Boolean = false,
  val lastStartedAt: Long? = null
)

@HiltViewModel
class ServiceViewModel @Inject constructor(
  private val startService: StartServiceUseCase,
  private val endService: EndServiceUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(ServiceUiState())
  val uiState = _uiState.asStateFlow()

  private val _events = MutableSharedFlow<UiEvent>()
  val events = _events.asSharedFlow()

  fun onStart(routeId: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      when (val res = startService(routeId)) {
        is Result.Success -> {
          _uiState.update { it.copy(isActive = true, isLoading = false, lastStartedAt = System.currentTimeMillis()) }
          _events.emit(UiEvent.ShowSnackbar("Servis başlatıldı"))
        }
        is Result.Error -> {
          _uiState.update { it.copy(isLoading = false) }
          _events.emit(UiEvent.ShowSnackbar("Başlatma başarısız: ${res.throwable.message ?: ""}"))
        }
        is Result.Loading -> {}
      }
    }
  }

  fun onEnd() {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      when (val res = endService()) {
        is Result.Success -> {
          _uiState.update { it.copy(isActive = false, isLoading = false) }
          _events.emit(UiEvent.ShowSnackbar("Servis bitirildi"))
        }
        is Result.Error -> {
          _uiState.update { it.copy(isLoading = false) }
          _events.emit(UiEvent.ShowSnackbar("Bitirme başarısız: ${res.throwable.message ?: ""}"))
        }
        is Result.Loading -> {}
      }
    }
  }
}
```

### 9.2) UI — ServiceScreen
`app/src/main/java/com/ozyuce/maps/feature/service/ServiceScreen.kt`
```kotlin
package com.ozyuce.maps.feature.service

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ozyuce.maps.core.common.ui.UiEvent
import com.ozyuce.maps.ui.CollectLatest

@Composable
fun ServiceScreen(nav: NavController, routeId: String = "default", vm: ServiceViewModel = hiltViewModel()) {
  val ui = vm.uiState.collectAsState().value
  val snackbar = remember { SnackbarHostState() }

  Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { pad ->
    Column(
      Modifier.padding(pad).fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Text(if (ui.isActive) "Servis AKTİF" else "Servis PASİF", style = MaterialTheme.typography.headlineSmall)
      Spacer(Modifier.height(24.dp))
      Button(
        onClick = { vm.onStart(routeId) },
        enabled = !ui.isLoading && !ui.isActive,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp)
      ) { Text("Başlat") }

      Spacer(Modifier.height(12.dp))

      OutlinedButton(
        onClick = vm::onEnd,
        enabled = !ui.isLoading && ui.isActive,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp)
      ) { Text("Bitir") }
    }
  }

  CollectLatest(vm.events) { e ->
    if (e is UiEvent.ShowSnackbar) { LaunchedEffect(e.message) { snackbar.showSnackbar(e.message) } }
  }
}
```

### 9.3) NavGraph entegrasyonu
`app/src/main/java/com/ozyuce/maps/navigation/AppNavGraph.kt` içine:
```kotlin
composable(Dest.Service.route) { ServiceScreen(navController) }
```

---

## 10) Stops + Personnel — Listeleme ve kontrol

### 10.1) ViewModel
`app/src/main/java/com/ozyuce/maps/feature/stops/StopsViewModel.kt`
```kotlin
package com.ozyuce.maps.feature.stops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozyuce.maps.core.common.ui.UiEvent
import com.ozyuce.maps.core.common.result.Result
import com.ozyuce.maps.domain.repository.Personnel
import com.ozyuce.maps.domain.repository.Stop
import com.ozyuce.maps.domain.usecase.CheckStopUseCase
import com.ozyuce.maps.domain.usecase.GetPersonnelUseCase
import com.ozyuce.maps.domain.usecase.GetStopsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StopsUiState(
  val stops: List<Stop> = emptyList(),
  val personnel: List<Personnel> = emptyList(),
  val isLoading: Boolean = false
)

@HiltViewModel
class StopsViewModel @Inject constructor(
  private val getStops: GetStopsUseCase,
  private val getPersonnel: GetPersonnelUseCase,
  private val checkStop: CheckStopUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow(StopsUiState())
  val uiState = _uiState.asStateFlow()

  private val _events = MutableSharedFlow<UiEvent>()
  val events = _events.asSharedFlow()

  fun load(routeId: String) {
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true) }
      val stopsRes = getStops(routeId)
      val persRes = getPersonnel(routeId)
      when (stopsRes) {
        is Result.Success -> _uiState.update { it.copy(stops = stopsRes.data) }
        is Result.Error -> _events.emit(UiEvent.ShowSnackbar("Duraklar alınamadı"))
        is Result.Loading -> {}
      }
      when (persRes) {
        is Result.Success -> _uiState.update { it.copy(personnel = persRes.data) }
        is Result.Error -> _events.emit(UiEvent.ShowSnackbar("Personel alınamadı"))
        is Result.Loading -> {}
      }
      _uiState.update { it.copy(isLoading = false) }
    }
  }

  fun toggleCheck(stopId: String, boarded: Boolean) {
    viewModelScope.launch {
      when (val res = checkStop(stopId, boarded)) {
        is Result.Success -> _events.emit(UiEvent.ShowSnackbar(if (boarded) "Yoklama OK" else "Yoklama iptal"))
        is Result.Error -> _events.emit(UiEvent.ShowSnackbar("İşlem başarısız"))
        is Result.Loading -> {}
      }
    }
  }
}
```

### 10.2) UI — StopsScreen
`app/src/main/java/com/ozyuce/maps/feature/stops/StopsScreen.kt`
```kotlin
package com.ozyuce.maps.feature.stops

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ozyuce.maps.core.common.ui.UiEvent
import com.ozyuce.maps.ui.CollectLatest

@Composable
fun StopsScreen(nav: NavController, routeId: String = "default", vm: StopsViewModel = hiltViewModel()) {
  val ui = vm.uiState.collectAsState().value
  val snackbar = remember { SnackbarHostState() }

  LaunchedEffect(routeId) { vm.load(routeId) }

  Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { pad ->
    Column(Modifier.padding(pad).fillMaxSize().padding(12.dp)) {
      Text("Personel", style = MaterialTheme.typography.titleLarge)
      Spacer(Modifier.height(8.dp))
      LazyColumn(Modifier.fillMaxSize()) {
        items(ui.personnel) { p ->
          Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
              Column(Modifier.weight(1f)) {
                Text(p.name, style = MaterialTheme.typography.titleMedium)
                Text(if (p.active) "Aktif" else "Pasif", style = MaterialTheme.typTypography.bodySmall)
              }
              Row {
                IconButton(onClick = { vm.toggleCheck(stopId = p.stopId ?: "", boarded = true) }) {
                  Icon(Icons.Default.Check, contentDescription = "Check-in")
                }
                IconButton(onClick = { vm.toggleCheck(stopId = p.stopId ?: "", boarded = false) }) {
                  Icon(Icons.Default.Close, contentDescription = "Uncheck")
                }
              }
            }
          }
        }
      }
    }
  }

  CollectLatest(vm.events) { e ->
    if (e is UiEvent.ShowSnackbar) { LaunchedEffect(e.message) { snackbar.showSnackbar(e.message) } }
  }
}
```

### 10.3) NavGraph entegrasyonu
`app/src/main/java/com/ozyuce/maps/navigation/AppNavGraph.kt` içine:
```kotlin
composable(Dest.Stops.route) { StopsScreen(navController) }
```

---

## Kabul ölçütleri
- `./gradlew :app:assembleDevDebug` başarı.
- LoginScreen üzerinden başarıyla giriş → Dashboard’a nav olur (fake repo impl dahi olsa).
- ServiceScreen “Başlat/Bitir” butonları UI state’i doğru günceller ve snackbar gösterir.
- StopsScreen personel listesini gösterir; Check/X butonları çağrılarını yapar ve snackbar gösterir.

## Notlar
- Gerçek API entegrasyonu ve WebSocket/konum akışı **Adım 11**'de tamamlanacak.
- Eğer feature’lar ayrı Gradle modülleriyse, yolları ilgili modülün `src/main/java` altına taşı.
