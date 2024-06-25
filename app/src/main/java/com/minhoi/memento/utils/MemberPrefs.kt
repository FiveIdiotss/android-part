package com.minhoi.memento.utils

import android.content.Context
import android.content.SharedPreferences
import com.minhoi.memento.data.dto.MemberDTO

class MemberPrefs(context: Context) {
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    init {
        sharedPreference = context.getSharedPreferences("user", Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
    }

    fun setMemberPrefs(value: MemberDTO) {
        editor.putLong("id", value.id).apply()
        editor.putString("email", value.email).apply()
        editor.putString("name", value.name).apply()
        editor.putInt("year", value.year).apply()
        editor.putString("gender", value.gender).apply()
        editor.putString("schoolName", value.schoolName).apply()
        editor.putString("majorName", value.majorName).apply()
        editor.putString("profileImageUrl", value.profileImageUrl).apply()
        editor.putInt("consultCount", value.consultCount).apply()
    }

    fun getMemberPrefs(): MemberDTO {
        val id = sharedPreference.getLong("id", 0)
        val email = sharedPreference.getString("email", "")
        val name = sharedPreference.getString("name", "")
        val year = sharedPreference.getInt("year", 0)
        val gender = sharedPreference.getString("gender", "")
        val schoolName = sharedPreference.getString("schoolName", "")
        val majorName = sharedPreference.getString("majorName", "")
        val profileImageUrl = sharedPreference.getString("profileImageUrl", "")
        val consultCount = sharedPreference.getInt("consultCount", 0)
        return MemberDTO(id, email!!, name!!, year, gender!!, schoolName!!, majorName!!, profileImageUrl!!, consultCount)
    }
}