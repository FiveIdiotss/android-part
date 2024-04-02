package com.minhoi.memento

import android.app.Application
import com.minhoi.memento.utils.MemberPrefs
import com.minhoi.memento.utils.TokenPrefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MentoApplication : Application() {
    companion object {
        lateinit var prefs: TokenPrefs
        lateinit var memberPrefs: MemberPrefs
    }

    override fun onCreate() {
        super.onCreate()

        prefs = TokenPrefs(applicationContext)
        memberPrefs = MemberPrefs(applicationContext)
    }

}