@file:Suppress("DSL_SCOPE_VIOLATION")
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization")
}


android {
    buildFeatures {
        buildConfig = true
    }

    namespace = "com.ozyuce.maps.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://dev.ozyuce.maps/api/\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)
    implementation(libs.retrofitSerializationConverter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinxSerializationJson)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}


kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
