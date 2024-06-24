package com.minhoi.memento.data.dto.chat

import com.minhoi.memento.data.model.ChatMessageType

data class Sender(
    override val chatId: Long,
    override val name: String,
    override val content: String?,
    override var date: String,
    override val type: ChatMessageType,
    override val fileUrl: String?,
    override val senderId: Long,
    override val readCount: Int,
    override var showMinute: Boolean = true,
) : ChatMessage
