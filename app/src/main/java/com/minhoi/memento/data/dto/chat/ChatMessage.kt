package com.minhoi.memento.data.dto.chat

import com.minhoi.memento.data.model.ChatFileType

interface ChatMessage {
    val type: ChatFileType
    val fileUrl: String?
    val id: Long
    val content: String?
    var date: String
    val name: String?
    var showMinute: Boolean
}