
# OzyuceMaps — Yamalar (Adım 7: Data Katmanı + TokenProvider Implementasyonu + Repo Bağları)

Bu yama **Data katmanı** parçalarını ekler: Retrofit API arayüzleri, DTO↔Domain mapper’lar,
Room veritabanı (entity/dao/db), **DataStore tabanlı AuthTokenProvider implementasyonu**, 
token’ı header’a ekleyen **AuthTokenInterceptor**, **Repository implementasyonları** ve 
**Hilt DI bağları**. Var olan kodu gereksiz değiştirme; **yalnız eksikleri uygula**.

> Not: Projede @KotlinxClient/@KotlinxRetrofit qualifier’ları varsa onları kullan.
> Yoksa NetworkQualifiers.kt bloğunu ekleyebilirsin (yoksa).

---

## 7.0) (Opsiyonel) Network Qualifier tanımları (YOKSA ekle)
`core/network/src/main/java/com/ozyuce/maps/core/network/di/NetworkQualifiers.kt`
```kotlin
package com.ozyuce.maps.core.network.di
import javax.inject.Qualifier

@Qualifier annotation class KotlinxClient
@Qualifier annotation class KotlinxRetrofit
```
> Mevcutta benzer qualifier’lar varsa BU DOSYAYI EKLEME.

---

## 7.1) DataStore tabanlı TokenProvider implementasyonu (app → data/auth)

`app/src/main/java/com/ozyuce/maps/data/auth/DataStoreAuthTokenProvider.kt`
```kotlin
package com.ozyuce.maps.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")
private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")

@Singleton
class DataStoreAuthTokenProvider @Inject constructor(
  @ApplicationContext private val context: Context
) : AuthTokenProvider {
  override suspend fun getToken(): String? =
    context.authDataStore.data.map { it[KEY_AUTH_TOKEN] }.first()

  override suspend fun setToken(token: String?) {
    context.authDataStore.edit { prefs ->
      if (token.isNullOrBlank()) prefs.remove(KEY_AUTH_TOKEN)
      else prefs[KEY_AUTH_TOKEN] = token
    }
  }
}
```

`app/src/main/java/com/ozyuce/maps/di/TokenModule.kt`
```kotlin
package com.ozyuce.maps.di

import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import com.ozyuce.maps.data.auth.DataStoreAuthTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenModule {
  @Binds @Singleton
  abstract fun bindAuthTokenProvider(impl: DataStoreAuthTokenProvider): AuthTokenProvider
}
```

---

## 7.2) AuthTokenInterceptor (app → data/network)

`app/src/main/java/com/ozyuce/maps/data/network/AuthTokenInterceptor.kt`
```kotlin
package com.ozyuce.maps.data.network

import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenInterceptor @Inject constructor(
  private val tokenProvider: AuthTokenProvider
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val token = runBlocking { tokenProvider.getToken() }
    val req = chain.request().newBuilder().apply {
      if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token")
    }.build()
    return chain.proceed(req)
  }
}
```

`app/src/main/java/com/ozyuce/maps/di/NetworkBindings.kt`
```kotlin
package com.ozyuce.maps.di

import com.ozyuce.maps.data.network.AuthTokenInterceptor
import com.ozyuce.maps.core.network.di.KotlinxClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkBindings {
  @Provides @Singleton @KotlinxClient
  fun provideKotlinxOkHttpClientWithAuth(base: OkHttpClient, auth: AuthTokenInterceptor): OkHttpClient =
    base.newBuilder().addInterceptor(auth).build()
}
```
> Yukarıdaki yöntem, core/network’teki mevcut OkHttpClient’ı **auth interceptor** ile zenginleştirir. 
> Eğer zaten auth interceptor sağlayan bir provider varsa, bu bloğu atla veya yalnızca eksikse uygula.

---

## 7.3) Retrofit API arayüzleri (core/network → api veya app → data/api)

**Tercih:** `core/network` altında API arayüzleri.

`core/network/src/main/java/com/ozyuce/maps/core/network/api/AuthApi.kt`
```kotlin
package com.ozyuce.maps.core.network.api

import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String)
data class TokenResponse(val token: String)

interface AuthApi {
  @POST("/auth/login") suspend fun login(@Body body: LoginRequest): TokenResponse
  @POST("/auth/register") suspend fun register(@Body body: RegisterRequest): TokenResponse
}
```

`core/network/src/main/java/com/ozyuce/maps/core/network/api/ServiceApi.kt`
```kotlin
package com.ozyuce.maps.core.network.api

import retrofit2.http.Body
import retrofit2.http.POST

data class StartServiceRequest(val routeId: String)
data class SimpleResponse(val ok: Boolean = true)

interface ServiceApi {
  @POST("/service/start") suspend fun start(@Body body: StartServiceRequest): SimpleResponse
  @POST("/service/end") suspend fun end(): SimpleResponse
}
```

`core/network/src/main/java/com/ozyuce/maps/core/network/api/StopsApi.kt`
```kotlin
package com.ozyuce.maps.core.network.api

import retrofit2.http.*

data class StopDto(val id: String, val name: String, val sequence: Int, val scheduledTime: String?)

interface StopsApi {
  @GET("/stops") suspend fun getStops(@Query("routeId") routeId: String): List<StopDto>
  @POST("/stops/check") suspend fun check(@Query("stopId") stopId: String, @Query("boarded") boarded: Boolean): SimpleResponse
}
```

`core/network/src/main/java/com/ozyuce/maps/core/network/api/PersonnelApi.kt`
```kotlin
package com.ozyuce.maps.core.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class PersonnelDto(val id: String, val name: String, val active: Boolean, val stopId: String?)

interface PersonnelApi {
  @GET("/personnel") suspend fun getPersonnel(@Query("routeId") routeId: String): List<PersonnelDto>
  @POST("/personnel") suspend fun add(@Body personnel: PersonnelDto): SimpleResponse
}
```

`core/network/src/main/java/com/ozyuce/maps/core/network/api/ReportsApi.kt`
```kotlin
package com.ozyuce.maps.core.network.api

import retrofit2.http.GET

data class DailyReportDto(val date: String, val totalRides: Int, val onTimeRate: Double)

interface ReportsApi {
  @GET("/reports/daily") suspend fun getDaily(): DailyReportDto
}
```

`core/network/src/main/java/com/ozyuce/maps/core/network/api/NotificationsApi.kt`
```kotlin
package com.ozyuce.maps.core.network.api

import retrofit2.http.Body
import retrofit2.http.POST

data class SaveTokenRequest(val token: String)

interface NotificationsApi {
  @POST("/notifications/token") suspend fun saveToken(@Body body: SaveTokenRequest): SimpleResponse
}
```

`app/src/main/java/com/ozyuce/maps/di/ApiModule.kt`
```kotlin
package com.ozyuce.maps.di

import com.ozyuce.maps.core.network.api.*
import com.ozyuce.maps.core.network.di.KotlinxRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
  @Provides @Singleton fun provideAuthApi(@KotlinxRetrofit retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
  @Provides @Singleton fun provideServiceApi(@KotlinxRetrofit retrofit: Retrofit): ServiceApi = retrofit.create(ServiceApi::class.java)
  @Provides @Singleton fun provideStopsApi(@KotlinxRetrofit retrofit: Retrofit): StopsApi = retrofit.create(StopsApi::class.java)
  @Provides @Singleton fun providePersonnelApi(@KotlinxRetrofit retrofit: Retrofit): PersonnelApi = retrofit.create(PersonnelApi::class.java)
  @Provides @Singleton fun provideReportsApi(@KotlinxRetrofit retrofit: Retrofit): ReportsApi = retrofit.create(ReportsApi::class.java)
  @Provides @Singleton fun provideNotificationsApi(@KotlinxRetrofit retrofit: Retrofit): NotificationsApi = retrofit.create(NotificationsApi::class.java)
}
```

---

## 7.4) Room veritabanı (app → data/local)

`app/src/main/java/com/ozyuce/maps/data/local/entity/StopEntity.kt`
```kotlin
package com.ozyuce.maps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stops")
data class StopEntity(
  @PrimaryKey val id: String,
  val name: String,
  val sequence: Int,
  val scheduledTime: String?
)
```

`app/src/main/java/com/ozyuce/maps/data/local/entity/PersonnelEntity.kt`
```kotlin
package com.ozyuce.maps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personnel")
data class PersonnelEntity(
  @PrimaryKey val id: String,
  val name: String,
  val active: Boolean,
  val stopId: String?
)
```

`app/src/main/java/com/ozyuce/maps/data/local/entity/ServiceSessionEntity.kt`
```kotlin
package com.ozyuce.maps.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_sessions")
data class ServiceSessionEntity(
  @PrimaryKey val id: String,
  val routeId: String,
  val startTime: Long,
  val endTime: Long?,
  val driverId: String
)
```

`app/src/main/java/com/ozyuce/maps/data/local/dao/StopsDao.kt`
```kotlin
package com.ozyuce.maps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ozyuce.maps.data.local.entity.StopEntity

@Dao
interface StopsDao {
  @Query("SELECT * FROM stops ORDER BY sequence")
  suspend fun getAll(): List<StopEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<StopEntity>)
}
```

`app/src/main/java/com/ozyuce/maps/data/local/dao/PersonnelDao.kt`
```kotlin
package com.ozyuce.maps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ozyuce.maps.data.local.entity.PersonnelEntity

@Dao
interface PersonnelDao {
  @Query("SELECT * FROM personnel")
  suspend fun getAll(): List<PersonnelEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAll(items: List<PersonnelEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: PersonnelEntity)
}
```

`app/src/main/java/com/ozyuce/maps/data/local/dao/ServiceSessionDao.kt`
```kotlin
package com.ozyuce.maps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ozyuce.maps.data.local.entity.ServiceSessionEntity

@Dao
interface ServiceSessionDao {
  @Query("SELECT * FROM service_sessions ORDER BY startTime DESC")
  suspend fun getAll(): List<ServiceSessionEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(item: ServiceSessionEntity)
}
```

`app/src/main/java/com/ozyuce/maps/data/local/AppDatabase.kt`
```kotlin
package com.ozyuce.maps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ozyuce.maps.data.local.dao.*
import com.ozyuce.maps.data.local.entity.*

@Database(
  entities = [StopEntity::class, PersonnelEntity::class, ServiceSessionEntity::class],
  version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun stopsDao(): StopsDao
  abstract fun personnelDao(): PersonnelDao
  abstract fun serviceSessionDao(): ServiceSessionDao
}
```

`app/src/main/java/com/ozyuce/maps/di/DatabaseModule.kt`
```kotlin
package com.ozyuce.maps.di

import android.content.Context
import androidx.room.Room
import com.ozyuce.maps.data.local.AppDatabase
import com.ozyuce.maps.data.local.dao.PersonnelDao
import com.ozyuce.maps.data.local.dao.ServiceSessionDao
import com.ozyuce.maps.data.local.dao.StopsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides @Singleton
  fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
    Room.databaseBuilder(ctx, AppDatabase::class.java, "ozyuce.db").build()

  @Provides fun provideStopsDao(db: AppDatabase): StopsDao = db.stopsDao()
  @Provides fun providePersonnelDao(db: AppDatabase): PersonnelDao = db.personnelDao()
  @Provides fun provideServiceSessionDao(db: AppDatabase): ServiceSessionDao = db.serviceSessionDao()
}
```

---

## 7.5) DTO ↔ Domain mapper’lar (app → data/mapper)

`app/src/main/java/com/ozyuce/maps/data/mapper/Mappers.kt`
```kotlin
package com.ozyuce.maps.data.mapper

import com.ozyuce.maps.core.network.api.*
import com.ozyuce.maps.data.local.entity.*
import com.ozyuce.maps.domain.repository.*

fun StopDto.toEntity() = StopEntity(id, name, sequence, scheduledTime)
fun StopDto.toDomain() = Stop(id, name, sequence, scheduledTime)

fun PersonnelDto.toEntity() = PersonnelEntity(id, name, active, stopId)
fun PersonnelDto.toDomain() = Personnel(id, name, active, stopId)

fun DailyReportDto.toDomain() = DailyReport(date, totalRides, onTimeRate)
```
> Gerektikçe genişlet.

---

## 7.6) Repository implementasyonları (app → data/repository)

**Not:** Projede özel Result sınıfı varsa, aşağıdaki dosyalarda **alias import** kullan:
`import com.ozyuce.maps.core.common.result.Result as AppResult`

`app/src/main/java/com/ozyuce/maps/data/repository/AuthRepositoryImpl.kt`
```kotlin
package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.common.auth.AuthTokenProvider
import com.ozyuce.maps.core.network.api.AuthApi
import com.ozyuce.maps.core.network.api.LoginRequest
import com.ozyuce.maps.core.network.api.RegisterRequest
import com.ozyuce.maps.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
  private val api: AuthApi,
  private val tokenProvider: AuthTokenProvider
) : AuthRepository {
  override suspend fun login(email: String, password: String): AppResult<Unit> = try {
    val resp = api.login(LoginRequest(email, password))
    tokenProvider.setToken(resp.token)
    AppResult.Success(Unit)
  } catch (t: Throwable) {
    AppResult.Error(t)
  }

  override suspend fun register(email: String, password: String): AppResult<Unit> = try {
    val resp = api.register(RegisterRequest(email, password))
    tokenProvider.setToken(resp.token)
    AppResult.Success(Unit)
  } catch (t: Throwable) {
    AppResult.Error(t)
  }

  override suspend fun logout(): AppResult<Unit> = try {
    tokenProvider.setToken(null)
    AppResult.Success(Unit)
  } catch (t: Throwable) {
    AppResult.Error(t)
  }
}
```

`app/src/main/java/com/ozyuce/maps/data/repository/ServiceRepositoryImpl.kt`
```kotlin
package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.network.api.ServiceApi
import com.ozyuce.maps.domain.repository.ServiceRepository
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
  private val api: ServiceApi
) : ServiceRepository {
  override suspend fun startService(routeId: String): AppResult<Unit> = try {
    api.start(com.ozyuce.maps.core.network.api.StartServiceRequest(routeId))
    AppResult.Success(Unit)
  } catch (t: Throwable) { AppResult.Error(t) }

  override suspend fun endService(): AppResult<Unit> = try {
    api.end()
    AppResult.Success(Unit)
  } catch (t: Throwable) { AppResult.Error(t) }
}
```

`app/src/main/java/com/ozyuce/maps/data/repository/StopsRepositoryImpl.kt`
```kotlin
package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.network.api.StopsApi
import com.ozyuce.maps.data.local.dao.StopsDao
import com.ozyuce.maps.data.mapper.toDomain
import com.ozyuce.maps.data.mapper.toEntity
import com.ozyuce.maps.domain.repository.Stop
import com.ozyuce.maps.domain.repository.StopsRepository
import javax.inject.Inject

class StopsRepositoryImpl @Inject constructor(
  private val api: StopsApi,
  private val dao: StopsDao
) : StopsRepository {
  override suspend fun getStops(routeId: String): AppResult<List<Stop>> = try {
    val dtos = api.getStops(routeId)
    dao.upsertAll(dtos.map { it.toEntity() })
    AppResult.Success(dtos.map { it.toDomain() })
  } catch (t: Throwable) { AppResult.Error(t) }

  override suspend fun checkStop(stopId: String, boarded: Boolean): AppResult<Unit> = try {
    api.check(stopId, boarded)
    AppResult.Success(Unit)
  } catch (t: Throwable) { AppResult.Error(t) }
}
```

`app/src/main/java/com/ozyuce/maps/data/repository/PersonnelRepositoryImpl.kt`
```kotlin
package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.network.api.PersonnelApi
import com.ozyuce.maps.data.local.dao.PersonnelDao
import com.ozyuce.maps.data.mapper.toDomain
import com.ozyuce.maps.data.mapper.toEntity
import com.ozyuce.maps.domain.repository.Personnel
import com.ozyuce.maps.domain.repository.PersonnelRepository
import javax.inject.Inject

class PersonnelRepositoryImpl @Inject constructor(
  private val api: com.ozyuce.maps.core.network.api.PersonnelApi,
  private val dao: PersonnelDao
) : PersonnelRepository {
  override suspend fun getPersonnel(routeId: String): AppResult<List<Personnel>> = try {
    val dtos = api.getPersonnel(routeId)
    dao.upsertAll(dtos.map { it.toEntity() })
    AppResult.Success(dtos.map { it.toDomain() })
  } catch (t: Throwable) { AppResult.Error(t) }

  override suspend fun addPersonnel(personnel: Personnel): AppResult<Unit> = try {
    val dto = com.ozyuce.maps.core.network.api.PersonnelDto(
      id = personnel.id, name = personnel.name, active = personnel.active, stopId = personnel.stopId
    )
    api.add(dto)
    dao.upsert(dto.toEntity())
    AppResult.Success(Unit)
  } catch (t: Throwable) { AppResult.Error(t) }
}
```

`app/src/main/java/com/ozyuce/maps/data/repository/ReportsRepositoryImpl.kt`
```kotlin
package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.network.api.ReportsApi
import com.ozyuce.maps.data.mapper.toDomain
import com.ozyuce.maps.domain.repository.DailyReport
import com.ozyuce.maps.domain.repository.ReportsRepository
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
  private val api: ReportsApi
) : ReportsRepository {
  override suspend fun getDailyReport(): AppResult<DailyReport> = try {
    AppResult.Success(api.getDaily().toDomain())
  } catch (t: Throwable) { AppResult.Error(t) }
}
```

`app/src/main/java/com/ozyuce/maps/data/repository/NotificationsRepositoryImpl.kt`
```kotlin
package com.ozyuce.maps.data.repository

import com.ozyuce.maps.core.common.result.Result as AppResult
import com.ozyuce.maps.core.network.api.NotificationsApi
import com.ozyuce.maps.core.network.api.SaveTokenRequest
import com.ozyuce.maps.domain.repository.NotificationsRepository
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
  private val api: NotificationsApi
) : NotificationsRepository {
  override suspend fun saveFcmToken(token: String): AppResult<Unit> = try {
    api.saveToken(SaveTokenRequest(token))
    AppResult.Success(Unit)
  } catch (t: Throwable) { AppResult.Error(t) }
}
```

---

## 7.7) Hilt Binds — Repository bindingleri (app → di)

`app/src/main/java/com/ozyuce/maps/di/RepositoryModule.kt`
```kotlin
package com.ozyuce.maps.di

import com.ozyuce.maps.data.repository.*
import com.ozyuce.maps.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
  @Binds @Singleton abstract fun bindServiceRepository(impl: ServiceRepositoryImpl): ServiceRepository
  @Binds @Singleton abstract fun bindStopsRepository(impl: StopsRepositoryImpl): StopsRepository
  @Binds @Singleton abstract fun bindPersonnelRepository(impl: PersonnelRepositoryImpl): PersonnelRepository
  @Binds @Singleton abstract fun bindReportsRepository(impl: ReportsRepositoryImpl): ReportsRepository
  @Binds @Singleton abstract fun bindNotificationsRepository(impl: NotificationsRepositoryImpl): NotificationsRepository
}
```

---

## Kabul ölçütleri
- `./gradlew :app:assembleDevDebug` başarı.
- Repository implementasyonları **derleniyor** ve Domain use-case’leriyle uyumlu.
- AuthTokenProvider implementasyonu DataStore ile çalışıyor; AuthTokenInterceptor header’a token ekliyor.
- Database/DAO’lar derleniyor; birim testler için hazır.

## Notlar
- WebSocket/konum/ETA entegrasyonları Adım 11’de ele alınacak.
- Mapper’lar ve DTO’lar minimal tutuldu; gerçek şemaya göre genişlet.
- `runBlocking` kullanımı interceptor’da kaçınılmaz; daha ileri seviye için in‑memory cache + listener düşünebiliriz.
