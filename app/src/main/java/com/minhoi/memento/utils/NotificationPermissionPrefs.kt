package com.minhoi.memento.utils

import android.content.Context
import android.content.SharedPreferences

class NotificationPermissionPrefs(context: Context) {

    private val sharedPreference: SharedPreferences = context.getSharedPreferences("notification", Context.MODE_PRIVATE)

    fun setChatPermission(isEnabled: Boolean) {
        sharedPreference.edit().putBoolean("chat", isEnabled).apply()
    }

    fun getChatPermission() = sharedPreference.getBoolean("chat", false)

}