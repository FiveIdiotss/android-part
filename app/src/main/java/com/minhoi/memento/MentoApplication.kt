package com.minhoi.memento

import android.app.Application
import com.minhoi.memento.Utils.TokenPrefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MentoApplication : Application() {
    companion object {
        lateinit var prefs: TokenPrefs
    }

    override fun onCreate() {
        super.onCreate()

        prefs = TokenPrefs(applicationContext)
    }

}