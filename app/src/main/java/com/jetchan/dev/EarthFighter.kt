package com.jetchan.dev

import android.app.Application
import timber.log.Timber

class EarthFighter : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}