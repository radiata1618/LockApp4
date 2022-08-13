package com.app.lockapp4.framework.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class LockAppApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
//        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }
}

//val app = (application as LockAppApplication)　で呼び出し