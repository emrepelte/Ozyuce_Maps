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
import javax.inject.Qualifier
import javax.inject.Singleton
import com.ozyuce.maps.core.network.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    annotation class KotlinxClient

    @Qualifier
    annotation class KotlinxRetrofit

    @Qualifier
    annotation class AuthInterceptor


    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

            @Provides
    @Singleton
    @KotlinxClient
    fun provideOkHttpClient(
        @AuthInterceptor authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {


                return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @KotlinxRetrofit
    fun provideRetrofit(@KotlinxClient client: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()
    }
}