package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("memberDTO")
    val memberDTO: MemberDto,

    @SerializedName("token")
    val token: String,

    @SerializedName("message")
    val message: String
)
