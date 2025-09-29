package com.ozyuce.maps

import android.app.Application
import com.ozyuce.maps.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OzyuceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG && Timber.forest().none { it is Timber.DebugTree }) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
