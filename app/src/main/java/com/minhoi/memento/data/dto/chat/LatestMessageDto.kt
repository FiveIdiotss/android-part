package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName

data class LatestMessageDto(
    @SerializedName("content")
    val content: String,
    @SerializedName("localDateTime")
    val localDateTime: String,
    @SerializedName("hasImage")
    val hasImage: Boolean
)

