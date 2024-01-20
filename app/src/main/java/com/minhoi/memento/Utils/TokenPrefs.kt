package com.minhoi.memento.Utils

import android.content.Context
import android.content.SharedPreferences

private const val ACCESS_TOKEN = "Access_Token"

class TokenPrefs(context: Context) {

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    init {
        sharedPreference = context.getSharedPreferences("token", Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
    }

    fun setAccessToken(value: String) {
        editor.putString(ACCESS_TOKEN, value).apply()
    }

    fun getAccessToken(defValue: String) = sharedPreference.getString(ACCESS_TOKEN, defValue)

}