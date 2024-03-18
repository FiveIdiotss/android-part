package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName

data class MessageDto(
    val senderId: Long,
    val content: String,
    @SerializedName("localDateTime") val date: String,
    val senderName: String
)
