package com.minhoi.memento.data.dto.join

import com.google.gson.annotations.SerializedName

data class EmailVerificationRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("univName")
    val univName: String
)
