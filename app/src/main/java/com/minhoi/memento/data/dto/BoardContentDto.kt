package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class BoardContentDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("school")
    val school: String,

    @SerializedName("major")
    val major: String,

    @SerializedName("year")
    val year: Int,

    @SerializedName("content")
    val content: String
)
