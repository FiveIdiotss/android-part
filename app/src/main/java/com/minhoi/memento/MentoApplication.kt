package com.minhoi.memento

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.FirebaseApp
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

        FirebaseApp.initializeApp(this)
        addFCMChannel()
    }

    // FCM Channel을 등록하는 함수
    private fun addFCMChannel() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val chatChannel = NotificationChannel(
            "CHAT_CHANNEL",
            getString(R.string.channel_chat),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_chat_description)
        }
        val postChannel = NotificationChannel(
            "POST_CHANNEL",
            getString(R.string.channel_post),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_post_description)
        }

        val channels = listOf(
            chatChannel,
            postChannel
        )
        notificationManager.createNotificationChannels(channels)
    }
}