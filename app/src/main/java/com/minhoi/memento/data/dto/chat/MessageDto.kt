package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.ChatFileType

data class MessageDto(
    val fileType: ChatFileType,
    val fileURL: String?,
    val senderId: Long,
    val chatRoomId: Long,
    val content: String?,
    @SerializedName("localDateTime") val date: String,
    val senderName: String,
)
