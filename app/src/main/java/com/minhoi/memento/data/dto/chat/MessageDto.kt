package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.ChatMessageType

data class MessageDto(
    val messageType: ChatMessageType,
    val fileURL: String?,
    val senderId: Long,
    val chatRoomId: Long,
    val content: String?,
    @SerializedName("localDateTime") val date: String,
    val senderName: String,
    val readCount: Int
) {
    fun toSender(): Sender = Sender(
        name = senderName,
        content = content,
        date = date,
        type = messageType,
        fileUrl = fileURL,
        id = senderId,
        readCount = readCount
    )

    fun toReceiver(): Receiver = Receiver(
        name = senderName,
        content = content,
        date = date,
        type = messageType,
        fileUrl = fileURL,
        id = senderId,
        readCount = readCount
    )
}