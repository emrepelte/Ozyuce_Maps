# OzyuceMaps — Önerilen Yamalar

Tarih: 2025-09-28T15:44:15


## app/build.gradle.kts (tam örnek)
```kotlin
plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("com.google.dagger.hilt.android")
  id("com.google.gms.google-services")
  kotlin("plugin.serialization")
}

android {
  namespace = "com.ozyuce.maps"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.ozyuce.maps"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  flavorDimensions += "env"
  productFlavors {
    create("dev")   { buildConfigField("String", "BASE_URL", ""https://dev.example.com/api"") }
    create("stage") { buildConfigField("String", "BASE_URL", ""https://stage.example.com/api"") }
    create("prod")  { buildConfigField("String", "BASE_URL", ""https://api.example.com"") }
  }

  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
  packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  // Compose BOM
  val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
  implementation(composeBom); androidTestImplementation(composeBom)

  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("androidx.navigation:navigation-compose:2.7.7")

  // Hilt
  implementation("com.google.dagger:hilt-android:2.51.1")
  kapt("com.google.dagger:hilt-android-compiler:2.51.1")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  // Retrofit + OkHttp
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

  // Serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

  // Room
  implementation("androidx.room:room-ktx:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")

  // DataStore
  implementation("androidx.datastore:datastore-preferences:1.1.1")

  // Location + Maps Compose
  implementation("com.google.android.gms:play-services-location:21.3.0")
  implementation("com.google.maps.android:maps-compose:4.3.0")

  // Firebase
  implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
  implementation("com.google.firebase:firebase-messaging")

  // Timber
  implementation("com.jakewharton.timber:timber:5.0.1")
}
```


## app/src/main/AndroidManifest.xml (örnek)
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ozyuce.maps">

  <uses-permission android:name="android.permission.INTERNET"/>
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
  <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
  <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION"/>

  <application
      android:name=".OzyuceApp"
      android:allowBackup="false"
      android:usesCleartextTraffic="false"
      android:theme="@style/Theme.OzyuceMaps">
    <activity
        android:name=".MainActivity"
        android:exported="true">
      <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
        <category android:name="android.intent.category.LAUNCHER"/>
      </intent-filter>
    </activity>

    <service
        android:name=".core.common.notification.OzyuceFirebaseMessagingService"
        android:exported="false">
      <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT"/>
      </intent-filter>
    </service>
  </application>
</manifest>
```

## Klasör iskeletleri
Oluşturulması önerilen dizinler:
- `core/network/`
- `feature/auth/`
- `feature/service/`

**İsteğe bağlı** örnek placeholder dosyaları (interface & entity):
```kotlin
// domain/repository/AuthRepository.kt
package com.ozyuce.maps.domain.repository
interface AuthRepository { suspend fun login(email:String, password:String): Result<Unit> }
```

```kotlin
// core/common/result/Result.kt
package com.ozyuce.maps.core.common.result
sealed class Result<out T> {
  data class Success<T>(val data:T): Result<T>()
  data class Error(val throwable: Throwable): Result<Nothing>()
  data object Loading: Result<Nothing>()
}
```


## Not: Firebase yapılandırması
`google-services.json` dosyasını `app/` klasörüne eklemeyi unutma; aksi halde FCM build adımında hata verebilir.
