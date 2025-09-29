# OzyuceMaps Proguard/R8 kuralları

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}

# Moshi
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-dontwarn org.jetbrains.annotations.**
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
-keep class androidx.hilt.** { *; }
-keep class dagger.hilt.** { *; }

# Maps & Location
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.**

# Firebase (opsiyonel)
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.measurement.** { *; }
-dontwarn com.google.firebase.**

# Domain Modeller (Entity ve DTO'lar)
-keep class com.ozyuce.maps.domain.model.** { *; }
-keep class com.ozyuce.maps.data.model.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# WebSocket
-keepclassmembers class * implements javax.websocket.Endpoint {
    @javax.websocket.OnMessage <methods>;
    @javax.websocket.OnOpen <methods>;
    @javax.websocket.OnClose <methods>;
    @javax.websocket.OnError <methods>;
}