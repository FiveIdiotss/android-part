package com.minhoi.memento.base

import com.google.gson.annotations.SerializedName

data class CommonResponse<out T>(
    @SerializedName("success")
    val isSuccess: Boolean = false,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T
)
