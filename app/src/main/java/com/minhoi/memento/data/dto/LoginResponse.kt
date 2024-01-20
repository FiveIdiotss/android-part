package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("memberId")
    val memberId: Int,

    @SerializedName("token")
    val token: String,

    @SerializedName("message")
    val message: String
)
