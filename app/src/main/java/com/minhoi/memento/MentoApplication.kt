package com.minhoi.memento

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.FirebaseApp
import com.minhoi.memento.utils.MemberPrefs
import com.minhoi.memento.utils.NotificationPermissionPrefs
import com.minhoi.memento.utils.TokenPrefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MentoApplication : Application() {
    companion object {
        lateinit var prefs: TokenPrefs
        lateinit var memberPrefs: MemberPrefs
        lateinit var notificationPermissionPrefs: NotificationPermissionPrefs
    }

    override fun onCreate() {
        super.onCreate()

        prefs = TokenPrefs(applicationContext)
        memberPrefs = MemberPrefs(applicationContext)
        notificationPermissionPrefs = NotificationPermissionPrefs(applicationContext)

        FirebaseApp.initializeApp(this)
        addFCMChannel()
    }

    // FCM Channel을 등록하는 함수
    private fun addFCMChannel() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val chatChannel = NotificationChannel(
            getString(R.string.channel_chat_id),
            getString(R.string.channel_chat_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_chat_description)
        }
        val replyChannel = NotificationChannel(
            getString(R.string.channel_reply_id),
            getString(R.string.channel_reply_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_reply_description)
        }
        val applyChannel = NotificationChannel(
            getString(R.string.channel_apply_id),
            getString(R.string.channel_apply_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_apply_description)
        }
        val matchingChannel = NotificationChannel(
            getString(R.string.channel_matching_id),
            getString(R.string.channel_matching_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_matching_description)
        }

        val channels = listOf(
            chatChannel,
            replyChannel,
            applyChannel,
            matchingChannel
        )
        notificationManager.createNotificationChannels(channels)
    }
}