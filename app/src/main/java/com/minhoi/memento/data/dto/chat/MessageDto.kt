package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.ChatMessageType
import com.minhoi.memento.utils.parseLocalDateTime

data class MessageDto(
    val chatId: Long,
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
        chatId = chatId,
        name = senderName,
        content = content,
        date = parseLocalDateTime(date),
        type = messageType,
        fileUrl = fileURL,
        senderId = senderId,
        readCount = readCount
    )

    fun toReceiver(): Receiver = Receiver(
        chatId = chatId,
        name = senderName,
        content = content,
        date = parseLocalDateTime(date),
        type = messageType,
        fileUrl = fileURL,
        senderId = senderId,
        readCount = readCount
    )
}