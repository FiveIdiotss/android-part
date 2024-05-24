package com.minhoi.memento.data.dto.chat

import com.minhoi.memento.data.model.ChatFileType

data class ChatDate(
    override val name: String?,
    override val content: String?,
    override var date: String,
    override var showMinute: Boolean = true,
    override val type: ChatFileType,
    override val fileUrl: String?,
    override val id: Long,
) : ChatMessage
