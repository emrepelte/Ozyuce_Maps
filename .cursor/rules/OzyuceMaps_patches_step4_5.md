# OzyuceMaps — Yamalar (Adım 4–5: Hilt Modülleri + Navigation İskeleti)

Bu yama **yalnız eksik olan** parçaları tamamlamak içindir. Mevcut kodu mümkün olduğunca koru.
Aşağıdaki dosyaları **yoksa oluştur**, varsa **sadece eksik blokları ekle/uyumlandır**.

---

## 4) Hilt kurulumu + modüller

### 4.1) Coroutine Dispatchers (core/common)

`core/common/src/main/java/com/ozyuce/maps/core/common/di/DispatcherModule.kt`
```kotlin
package com.ozyuce.maps.core.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier annotation class IoDispatcher
@Qualifier annotation class DefaultDispatcher
@Qualifier annotation class MainDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
  @Provides @Singleton @IoDispatcher fun provideIo(): CoroutineDispatcher = Dispatchers.IO
  @Provides @Singleton @DefaultDispatcher fun provideDefault(): CoroutineDispatcher = Dispatchers.Default
  @Provides @Singleton @MainDispatcher fun provideMain(): CoroutineDispatcher = Dispatchers.Main
}
```

### 4.2) NetworkModule (core/network)

`core/network/src/main/java/com/ozyuce/maps/core/network/di/NetworkModule.kt`
```kotlin
package com.ozyuce.maps.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton
import com.ozyuce.maps.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
  }

  @Provides @Singleton
  fun provideAuthInterceptor(
    // İleride gerçek token sağlayıcı eklenecek (DataStore/Repo). Şimdilik no-op.
    ): Interceptor = Interceptor { chain ->
      val original = chain.request()
      val builder = original.newBuilder()
      // TODO: token varsa ekle → builder.addHeader("Authorization", "Bearer $token")
      chain.proceed(builder.build())
    }

  @Provides @Singleton
  fun provideOkHttpClient(auth: Interceptor): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
      level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }
    return OkHttpClient.Builder()
      .addInterceptor(auth)
      .addInterceptor(logging)
      .build()
  }

  @Provides @Singleton
  fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .addConverterFactory(json.asConverterFactory(contentType))
      .client(client)
      .build()
  }
}
```

> Not: Gerçek `AuthInterceptor` için **Token sağlayıcı** bağlamasını ileride (Adım 6) ekleyeceğiz.

### 4.3) (Opsiyonel) Repository/UseCase Modülleri iskeleti

`app/src/main/java/com/ozyuce/maps/di/RepositoryModule.kt`
```kotlin
package com.ozyuce.maps.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// TODO: Domain arayüzleri ve Data implementasyonları eklendiğinde @Binds ile eşle.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  // @Binds abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
```

`app/src/main/java/com/ozyuce/maps/di/UseCaseModule.kt`
```kotlin
package com.ozyuce.maps.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
  // @Provides fun provideLoginUseCase(repo: AuthRepository) = LoginUseCase(repo)
}
```

---

## 5) Navigation Compose iskeleti

### 5.1) Rota tanımları (sealed Dest)
`app/src/main/java/com/ozyuce/maps/navigation/Dest.kt`
```kotlin
package com.ozyuce.maps.navigation

sealed class Dest(val route: String) {
  data object Splash : Dest("splash")
  data object Login : Dest("login")
  data object Dashboard : Dest("dashboard")
  data object Service : Dest("service")
  data object Stops : Dest("stops")
  data object Map : Dest("map")
  data object Reports : Dest("reports")
  data object Profile : Dest("profile")
}
```

### 5.2) NavHost ve iskelet ekranlar
`app/src/main/java/com/ozyuce/maps/navigation/AppNavGraph.kt`
```kotlin
package com.ozyuce.maps.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(navController: NavHostController) {
  NavHost(navController, startDestination = Dest.Dashboard.route) {
    composable(Dest.Login.route) { Text("Login (TODO)") }
    composable(Dest.Dashboard.route) { Text("Dashboard (TODO)") }
    composable(Dest.Service.route) { Text("Service (TODO)") }
    composable(Dest.Stops.route) { Text("Stops (TODO)") }
    composable(Dest.Map.route) { Text("Map (TODO)") }
    composable(Dest.Reports.route) { Text("Reports (TODO)") }
    composable(Dest.Profile.route) { Text("Profile (TODO)") }
  }
}
```

### 5.3) MainActivity içinde kullanım
`app/src/main/java/com/ozyuce/maps/MainActivity.kt` içindeki `setContent { ... }` bloğuna **ekle/uyarla**:
```kotlin
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.ozyuce.maps.navigation.AppNavGraph

// ...
setContent {
  val navController = rememberNavController()
  AppNavGraph(navController)
}
```

> Eğer mevcut bir tema/App composable’ın varsa, `AppNavGraph()` çağrısını onun içine yerleştir.

### 5.4) UiEvent iskeleti (tek-seferlik olaylar)
`core/common/src/main/java/com/ozyuce/maps/core/common/ui/UiEvent.kt`
```kotlin
package com.ozyuce.maps.core.common.ui

sealed interface UiEvent {
  data class Navigate(val route: String) : UiEvent
  data class ShowSnackbar(val message: String) : UiEvent
}
```

> Not: ViewModel’ler `SharedFlow<UiEvent>` üzerinden olay yayacak. Activity/Screen ise toplayıp `navController.navigate(...)` veya snackbar gösterecek.

---

## Kabul ölçütleri
- Modül ve dosyalar derleniyor: `./gradlew :app:assembleDevDebug`
- `MainActivity` açıldığında Compose içinde basit placeholder metinler görünüyor.
- Hilt annotation processing hatası yok (Repository/UseCase modülleri şimdilik boş kalabilir).
- NetworkModule `BASE_URL` ve converter’ı başarıyla sağlıyor (gerçek API kullanımına geçince çağrılacak).

## Notlar
- Token eklemek için Adım 6’da **AuthTokenProvider**/DataStore bağı ekleyeceğiz.
- Splash/Login akışını Adım 8’de gerçek ekrana dönüştüreceğiz.
