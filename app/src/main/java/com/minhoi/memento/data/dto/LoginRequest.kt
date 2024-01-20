package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    private val email: String,

    @SerializedName("pw")
    private val pw: String
)
