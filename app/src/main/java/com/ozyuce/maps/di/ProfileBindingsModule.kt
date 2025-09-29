package com.ozyuce.maps.di

import com.ozyuce.maps.feature.profile.AppProfileLogoutHandler
import com.ozyuce.maps.feature.profile.logout.ProfileLogoutHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileBindingsModule {

    @Binds
    @Singleton
    abstract fun bindProfileLogoutHandler(
        impl: AppProfileLogoutHandler
    ): ProfileLogoutHandler
}
