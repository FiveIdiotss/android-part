package com.minhoi.memento.data.dto.chat

import com.minhoi.memento.data.model.ChatFileType

data class FileUploadResponse(
    val fileType: ChatFileType,
    val fileURL: String,
    val content: String?,
    val senderName: String,
    val senderId: Long,
    val chatRoomId: Long,
    val localDateTime: String
)
