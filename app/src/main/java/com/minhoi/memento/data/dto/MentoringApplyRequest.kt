package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class MentoringApplyRequest(
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String
)
