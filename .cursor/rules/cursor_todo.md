# OzyuceMaps – Cursor Görev Listesi (adım adım)

Bu dosyayı sırayla Cursor’a yapıştırarak ilerle. Her görev, .cursorrules’taki kurallara **tam uyum** varsayımıyla yazıldı.

---

## 1) Proje iskeleti + bağımlılıklar
**Komut (Cursor’a yapıştır):**
> OzyuceMaps için Android Compose projesi kur. .cursorrules’a tam uy.  
> - Gradle (KTS) dosyalarını oluştur ve şunları ekle: Compose BOM + Material3, Navigation Compose, Hilt (hilt-android, hilt-compiler, hilt-navigation-compose), Room (runtime, ktx, compiler), Retrofit + Moshi (veya Kotlinx-Serialization), OkHttp (logging + WebSocket), DataStore, Timber, Google Play Services Location, Maps Compose, Firebase Messaging (BoM).  
> - `minSdk 24`, `compileSdk latest`, Kotlin JVM target 17.  
> - `BuildConfig.BASE_URL` için productFlavors: dev/stage/prod.  
> - Paket adı: `com.ozyuce.maps`.  
> **Acceptance:** Gradle sync temiz, app empty Compose ekranıyla derleniyor, Timber init ediliyor.

---

## 2) Manifest + izinler + Splash
**Komut:**
> Manifest ve tema ayarla:  
> - İzinler: `ACCESS_COARSE_LOCATION`, `ACCESS_FINE_LOCATION`, `POST_NOTIFICATIONS`. Arka plan konum şimdilik **yok**.  
> - Material3 Splash API ile “OzyuceMaps” başlıklı basit splash.  
> **Acceptance:** Uygulama açılıyor, crash yok, izinler manifest’te.

---

## 3) Katmanlar ve modül/dizin yapısı
**Komut:**
> Aşağıdaki klasörleri ve boş şablonları oluştur:  
> - `core/common`, `core/network`, `core/database`  
> - `feature/auth`, `feature/service`, `feature/stops`, `feature/reports`, `feature/map`  
> - `domain` ve `data` paketleri (interface/impl ayrımı)  
> Boş interface ve data class iskeletlerini ekle (repo arayüzleri vs.).  
> **Acceptance:** Tüm paketler mevcut, derleme yeşil.

---

## 4) Hilt kurulumu + modüller
**Komut:**
> Hilt’i kur: `@HiltAndroidApp` Application, `NetworkModule` (OkHttp + Retrofit + auth interceptor stub), `DatabaseModule` (Room DB + DAO’lar için boş provider), `RepositoryModule` (arayüz→impl binding), `UseCaseModule`.  
> **Acceptance:** App derleniyor, Hilt oluşturduğu sınıflarla hata yok, Timber plant Application’da.

---

## 5) Navigation Compose iskeleti
**Komut:**
> `Dest` sealed class (login, dashboard, service, stops, reports, map). `NavHost` ve `NavController` ayarla.  
> Her rota için geçici Composable ekran (“TODO”). Navigation yan etkilerini VM’den `UiEvent.Navigate` ile yönetmek için `EventReceiver` scaffold’ı ekle.  
> **Acceptance:** Uygulama açılıyor, login→dashboard dummy geçişi çalışıyor.

---

## 6) Domain sözleşmeleri (Auth/Service/Stops/Reports/Notifications)
**Komut:**
> Domain katmanında şu arayüz ve use case’leri oluştur:  
> - Repos: `AuthRepository`, `ServiceRepository`, `StopsRepository`, `PersonnelRepository`, `ReportsRepository`, `NotificationsRepository`.  
> - UseCases: `LoginUseCase`, `RegisterUseCase`, `StartServiceUseCase`, `EndServiceUseCase`, `CheckStopUseCase`, `ListPersonnelUseCase`, `AddPersonnelUseCase`, `ComputeEtaUseCase`, `GetDailyReportUseCase`, `RegisterFcmTokenUseCase`.  
> Hepsi `Result<T>` döndürsün. Domain modellerini ekle (User, Token, ServiceSession, Stop, Personnel, DailyReport).  
> **Acceptance:** Domain test edilebilir, Android bağımlılığı yok.

---

## 7) Data katmanı (Retrofit/Room/Mapper)
**Komut:**
> - Retrofit API’ler: `AuthApi`, `ServiceApi`, `StopsApi`, `PersonnelApi`, `ReportsApi`, `NotificationsApi` (endpoint’ler: /auth/login,/auth/register,/service/start,/service/end,/stops/check,/stops,/personnel,/reports/daily,/notifications/token).  
> - DTO↔Domain mapper’ları ekle.  
> - Room: `StopEntity`, `PersonnelEntity`, `ServiceSessionEntity` + DAO’lar.  
> - Repository impl’leri yaz; domain model döndür.  
> - Token’ı DataStore’da sakla; interceptor header eklesin.  
> **Acceptance:** Mock API stub ile repo çağrıları derleniyor.

---

## 8) Auth özelliği (UI + VM)
**Komut:**
> `feature/auth`: `AuthViewModel` (StateFlow + UiEvent), `LoginScreen` (email+şifre), `RegisterScreen` (opsiyonel).  
> Başarılı login → `Dest.Dashboard`. Hatalarda `ShowSnackbar`.  
> **Acceptance:** Dummy backend ile login akışı çalışır şekilde (şimdilik fake success).

---

## 9) Servis Başlat/Bitir
**Komut:**
> `feature/service`:  
> - `ServiceViewModel` (isActive, startTime, isLoading, error).  
> - `ServiceScreen` iki büyük buton: “Başlat” `POST /service/start`, “Bitir” `POST /service/end`.  
> - Lokal olarak `ServiceSessionEntity`’ye kaydet.  
> **Acceptance:** Başlat/bitir çağrıları state’i güncelliyor, hata durumunda snackbar.

---

## 10) Durak Kontrolü + Personel
**Komut:**
> `feature/stops`:  
> - VM: `StopsViewModel(stops, personnel, isLoading)`  
> - UI: LazyColumn personel listesi, her satırda yeşil Check / kırmızı X `IconButton` → `POST /stops/check`.  
> - “+ Yeni Personel” diyalogu → `POST /personnel`.  
> - Stops/personnel `GET` ile yüklenir; Room’a cache’le.  
> **Acceptance:** işaretleme UI’sı çalışıyor, optimistic update + hata geri al.

---

## 11) Harita + Canlı konum + ETA
**Komut:**
> `feature/map`:  
> - Google Maps Compose entegrasyonu; konum izin akışı.  
> - Sürücü konumunu FusedLocationProvider’dan al ve VM akışına koy.  
> - OkHttp WebSocket client: konumu periyodik gönder (driver rolü).  
> - ETA için `ComputeEtaUseCase` ile Distance Matrix çağrısı; harita üzerinde rozet.  
> **Acceptance:** Harita görünüyor, marker hareket ediyor (fake path olabilir), ETA gösteriliyor.

---

## 12) Raporlama (Pie + Bar)
**Komut:**
> `feature/reports`:  
> - `ReportsViewModel` → `/reports/daily` çağrısı.  
> - Compose Canvas ile basit pie ve bar grafiklerini çiz (veya interop ile MPAndroidChart).  
> - Card’larda metrikler: toplam_personel, kullanan, katılmayan, ortalama_süre, mesafe.  
> **Acceptance:** Fake/real JSON ile grafikler render alıyor.

---

## 13) Bildirim (FCM)
**Komut:**
> FCM entegrasyonu:  
> - `FirebaseMessagingService` implementasyonu, token yenilemede `RegisterFcmTokenUseCase`.  
> - Notification channel’lar (8+).  
> - Uygulama içi kritik uyarılar için `UiEvent.ShowSnackbar` veya `AlertDialog`.  
> **Acceptance:** Test bildirimleri geliyor, token backend’e kaydoluyor.

---

## 14) Testler
**Komut:**
> Test kur:  
> - Domain: Use case unit test (JUnit + Turbine).  
> - Data: Repo/mapper test, Retrofit için `MockWebServer`, Room in-memory DAO test.  
> - Presentation: ViewModel test (fake repo), Compose UI test (semantics).  
> **Acceptance:** `./gradlew test` yeşil, örnek 6–8 test geçiyor.

---

## 15) Build flavors, proguard, release
**Komut:**
> - dev/stage/prod flavors; `BASE_URL`’ler.  
> - R8/proguard ayarları; shrink/optimize/obfuscate aktif.  
> - SigningConfig placeholder’ları.  
> **Acceptance:** `assembleDevDebug` ve `bundleProdRelease` başarılı.

---

## 16) Hata payı & Temizlik
**Komut:**
> - Timber seviyelerini gözden geçir; hassas veriyi loglama.  
> - Route string’leri tek merkezde; hardcode kaçaklarını düzelt.  
> - Dosya adı konvansiyonu (`lowercase_with_underscores`) ve paket hiyerarşisini doğrula.  
> **Acceptance:** .cursorrules’a göre linter/inspection temiz.

---

## Gidişat şablonu (her adımda kullan)
**Şablon:**
> **Görev:** (kısa başlık)  
> **Bağlam:** OzyuceMaps, .cursorrules’a uy.  
> **İşler:** (madde madde yapılacaklar)  
> **Kısıtlar:** XML yok, Navigation yan etkileri VM’den, immutable UI state, sealed UiEvent.  
> **Acceptance:** (ölçülebilir çıktılar)  
> **Ardından:** (hemen sonraki adımın kısa notu)

---

### Notlar
- Her büyük adımdan sonra derleme ve kısa manuel test yap.  
- Backend endpoint şemaları değişirse `.cursorrules` içindeki “Proje Bağlamı” bölümünü güncelle ve ilgili DTO/mapper’ları senkronize et.  
- Prod’a çıkmadan önce konum/notification izin akışlarını gerçek cihazda dene.

