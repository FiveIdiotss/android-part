package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class MemberDTO(
    @SerializedName("id")
    val id: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("year")
    val year: Int,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("schoolName")
    val schoolName: String,
    @SerializedName("majorName")
    val majorName: String,
    )
