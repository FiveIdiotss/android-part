package com.minhoi.memento.data.dto.chat

import com.minhoi.memento.data.model.ChatMessageType

data class FileUploadResponse(
    val fileType: ChatMessageType,
    val fileURL: String,
    val content: String?,
    val senderName: String,
    val senderId: Long,
    val chatRoomId: Long,
    val readCount: Int,
    val localDateTime: String
)
