package com.minhoi.memento.data.dto.chat

import com.minhoi.memento.data.model.ChatMessageType

interface ChatMessage {
    val chatId: Long
    val type: ChatMessageType
    val fileUrl: String?
    val senderId: Long
    val content: String?
    var date: String
    val name: String?
    var showMinute: Boolean
    val readCount: Int
}