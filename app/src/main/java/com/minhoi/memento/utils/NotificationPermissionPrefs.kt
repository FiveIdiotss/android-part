package com.minhoi.memento.utils

import android.content.Context
import android.content.SharedPreferences

class NotificationPermissionPrefs(context: Context) {

    private val sharedPreference: SharedPreferences = context.getSharedPreferences("notification", Context.MODE_PRIVATE)

    fun setChatPermission(isEnabled: Boolean) {
        sharedPreference.edit().putBoolean("chat", isEnabled).apply()
    }

    fun setApplyPermission(isEnabled: Boolean) {
        sharedPreference.edit().putBoolean("apply", isEnabled).apply()
    }

    fun setReplyPermission(isEnabled: Boolean) {
        sharedPreference.edit().putBoolean("reply", isEnabled).apply()
    }

    fun setMatchPermission(isEnabled: Boolean) {
        sharedPreference.edit().putBoolean("match", isEnabled).apply()
    }

    fun getChatPermission() = sharedPreference.getBoolean("chat", false)
    fun getApplyPermission() = sharedPreference.getBoolean("apply", false)
    fun getReplyPermission() = sharedPreference.getBoolean("reply", false)
    fun getMatchPermission() = sharedPreference.getBoolean("match", false)

}