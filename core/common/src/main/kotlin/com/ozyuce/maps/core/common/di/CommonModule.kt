package com.ozyuce.maps.core.common.di

import com.google.firebase.messaging.FirebaseMessaging
import com.ozyuce.maps.core.common.notification.NotificationRepository
import com.ozyuce.maps.core.common.notification.NotificationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        firebaseMessaging: FirebaseMessaging
    ): NotificationRepository {
        return NotificationRepositoryImpl(firebaseMessaging)
    }
}
