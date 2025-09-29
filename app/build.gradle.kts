@file:Suppress("DSL_SCOPE_VIOLATION")
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

val hasGoogleServicesJson = file("google-services.json").exists()


if (hasGoogleServicesJson) {
    apply(plugin = "com.google.gms.google-services")
}

apply(from = "version.gradle")

val versionCodeValue = when (val result = (extra["getVersionCode"] as? Closure<*>)?.call()) {
    is Number -> result.toInt()
    is String -> result.toIntOrNull() ?: 1
    else -> 1
}
val versionNameValue = (extra["getVersionNameFromGit"] as? Closure<*>)?.call()?.toString() ?: "1.0.0"

android {
    namespace = "com.ozyuce.maps"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ozyuce.maps"
        minSdk = 24
        targetSdk = 36
        versionCode = versionCodeValue
        versionName = versionNameValue
        testInstrumentationRunner = "com.ozyuce.maps.HiltTestRunner"

        manifestPlaceholders.putAll(
            mapOf(
                "MAPS_API_KEY" to (project.findProperty("MAPS_API_KEY") as? String ?: "")
            )
        )
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("boolean", "ANALYTICS_ENABLED", "false")
            manifestPlaceholders.putAll(
                mapOf(
                    "crashlyticsEnabled" to false,
                    "analyticsEnabled" to false
                )
            )
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "ANALYTICS_ENABLED", "true")
            manifestPlaceholders.putAll(
                mapOf(
                    "crashlyticsEnabled" to true,
                    "analyticsEnabled" to true
                )
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://dev.ozyuce.maps/api/\""
            )
        }
        create("stage") {
            dimension = "env"
            applicationIdSuffix = ".stage"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://stage.ozyuce.maps/api/\""
            )
        }
        create("prod") {
            dimension = "env"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://ozyuce.maps/api/\""
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Modules
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":data"))

    // Feature Modules
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:stops"))
    implementation(project(":feature:map"))
    implementation(project(":feature:reports"))
    implementation(project(":feature:profile"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core.splashscreen)

    // Compose BOM & UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofitSerializationConverter)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    kapt(libs.moshi.codegen)

    // Serialization
    implementation(libs.kotlinxSerializationJson)

    // DataStore
    implementation(libs.androidx.datastore)

    // Timber
    implementation(libs.timber)

    // Google Play Services & Maps
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.room.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
