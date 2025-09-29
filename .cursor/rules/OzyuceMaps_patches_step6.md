
# OzyuceMaps — Yamalar (Adım 6: Domain sözleşmeleri + TokenProvider iskeleti)

Bu yama **yalnız eksik olan** domain sözleşmelerini ve TokenProvider arayüzünü ekler.
Mevcut kodu koru; dosya varsa **sadece eksik kısımları** tamamla.

---

## 6.1) Ortak Sonuç türü (isteğe bağlı, yoksa ekle)

`core/common/src/main/java/com/ozyuce/maps/core/common/result/AppResult.kt`
```kotlin
package com.ozyuce.maps.core.common.result
// Projede hâlihazırda Result/Either varsa BUNU EKLEME.
// Yoksa, use-case'lerde kullanmak üzere basit bir alias ekliyoruz.
typealias AppResult<T> = kotlin.Result<T>
```

---

## 6.2) Token Provider arayüzü (core/common)

`core/common/src/main/java/com/ozyuce/maps/core/common/auth/AuthTokenProvider.kt`
```kotlin
package com.ozyuce.maps.core.common.auth

interface AuthTokenProvider {
  suspend fun getToken(): String?
  suspend fun setToken(token: String?)
}
```
> Not: Gerçek DataStore tabanlı implementasyon **Adım 7**'de gelecek. Şimdilik sadece arayüz.

---

## 6.3) Domain repository arayüzleri (app → domain)

Aşağıdaki dosyaları **yalnızca yoksa** oluştur.

`app/src/main/java/com/ozyuce/maps/domain/repository/AuthRepository.kt`
```kotlin
package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.AppResult

interface AuthRepository {
  suspend fun login(email: String, password: String): AppResult<Unit>
  suspend fun register(email: String, password: String): AppResult<Unit>
  suspend fun logout(): AppResult<Unit>
}
```

`app/src/main/java/com/ozyuce/maps/domain/repository/ServiceRepository.kt`
```kotlin
package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.AppResult

interface ServiceRepository {
  suspend fun startService(routeId: String): AppResult<Unit>
  suspend fun endService(): AppResult<Unit>
}
```

`app/src/main/java/com/ozyuce/maps/domain/repository/StopsRepository.kt`
```kotlin
package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.AppResult

data class Stop(val id: String, val name: String, val sequence: Int, val scheduledTime: String? = null)

interface StopsRepository {
  suspend fun getStops(routeId: String): AppResult<List<Stop>>
  suspend fun checkStop(stopId: String, boarded: Boolean): AppResult<Unit>
}
```

`app/src/main/java/com/ozyuce/maps/domain/repository/PersonnelRepository.kt`
```kotlin
package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.AppResult

data class Personnel(val id: String, val name: String, val active: Boolean, val stopId: String? = null)

interface PersonnelRepository {
  suspend fun getPersonnel(routeId: String): AppResult<List<Personnel>>
  suspend fun addPersonnel(personnel: Personnel): AppResult<Unit>
}
```

`app/src/main/java/com/ozyuce/maps/domain/repository/ReportsRepository.kt`
```kotlin
package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.AppResult

data class DailyReport(val date: String, val totalRides: Int, val onTimeRate: Double)

interface ReportsRepository {
  suspend fun getDailyReport(): AppResult<DailyReport>
}
```

`app/src/main/java/com/ozyuce/maps/domain/repository/NotificationsRepository.kt`
```kotlin
package com.ozyuce.maps.domain.repository

import com.ozyuce.maps.core.common.result.AppResult

interface NotificationsRepository {
  suspend fun saveFcmToken(token: String): AppResult<Unit>
}
```

---

## 6.4) Use-case sınıfları (app → domain/usecase)

`app/src/main/java/com/ozyuce/maps/domain/usecase/LoginUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
  private val repo: AuthRepository
) {
  suspend operator fun invoke(email: String, password: String): AppResult<Unit> =
    repo.login(email, password)
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/RegisterUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
  private val repo: AuthRepository
) {
  suspend operator fun invoke(email: String, password: String): AppResult<Unit> =
    repo.register(email, password)
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/StartServiceUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.ServiceRepository
import javax.inject.Inject

class StartServiceUseCase @Inject constructor(
  private val repo: ServiceRepository
) {
  suspend operator fun invoke(routeId: String): AppResult<Unit> = repo.startService(routeId)
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/EndServiceUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.ServiceRepository
import javax.inject.Inject

class EndServiceUseCase @Inject constructor(
  private val repo: ServiceRepository
) {
  suspend operator fun invoke(): AppResult<Unit> = repo.endService()
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/GetStopsUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.StopsRepository
import com.ozyuce.maps.domain.repository.Stop
import javax.inject.Inject

class GetStopsUseCase @Inject constructor(
  private val repo: StopsRepository
) {
  suspend operator fun invoke(routeId: String): AppResult<List<Stop>> = repo.getStops(routeId)
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/CheckStopUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.StopsRepository
import javax.inject.Inject

class CheckStopUseCase @Inject constructor(
  private val repo: StopsRepository
) {
  suspend operator fun invoke(stopId: String, boarded: Boolean): AppResult<Unit> =
    repo.checkStop(stopId, boarded)
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/GetPersonnelUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.PersonnelRepository
import com.ozyuce.maps.domain.repository.Personnel
import javax.inject.Inject

class GetPersonnelUseCase @Inject constructor(
  private val repo: PersonnelRepository
) {
  suspend operator fun invoke(routeId: String): AppResult<List<Personnel>> = repo.getPersonnel(routeId)
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/AddPersonnelUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.PersonnelRepository
import com.ozyuce.maps.domain.repository.Personnel
import javax.inject.Inject

class AddPersonnelUseCase @Inject constructor(
  private val repo: PersonnelRepository
) {
  suspend operator fun invoke(personnel: Personnel): AppResult<Unit> = repo.addPersonnel(personnel)
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/GetDailyReportUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.ReportsRepository
import com.ozyuce.maps.domain.repository.DailyReport
import javax.inject.Inject

class GetDailyReportUseCase @Inject constructor(
  private val repo: ReportsRepository
) {
  suspend operator fun invoke(): AppResult<DailyReport> = repo.getDailyReport()
}
```

`app/src/main/java/com/ozyuce/maps/domain/usecase/SaveFcmTokenUseCase.kt`
```kotlin
package com.ozyuce.maps.domain.usecase

import com.ozyuce.maps.core.common.result.AppResult
import com.ozyuce.maps.domain.repository.NotificationsRepository
import javax.inject.Inject

class SaveFcmTokenUseCase @Inject constructor(
  private val repo: NotificationsRepository
) {
  suspend operator fun invoke(token: String): AppResult<Unit> = repo.saveFcmToken(token)
}
```

---

## 6.5) Kabul ölçütleri
- Proje hâlâ derleniyor: `./gradlew :app:assembleDevDebug`.
- Hilt grafında yeni binding zorunlulukları YOK (çünkü bu adımda DI sağlayıcı eklemedik).
- `AuthTokenProvider` sadece arayüz olarak mevcut; gerçek implementasyon **Adım 7**'de eklenecek.

## Notlar
- Bir sonraki adımda (Adım 7), DataStore temelli `AuthTokenProvider` implementasyonu ve
  Retrofit/Room/Mapper/Repo impl bağları eklenerek use-case’ler DI ile sağlanacak.
