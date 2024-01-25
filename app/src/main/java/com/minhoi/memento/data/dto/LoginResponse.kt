package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("memberDTO")
    val memberDTO: MemberDto,

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("message")
    val message: String
)
