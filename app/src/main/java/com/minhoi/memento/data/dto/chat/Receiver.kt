package com.minhoi.memento.data.dto.chat

import com.minhoi.memento.data.model.ChatFileType

data class Receiver(
    override val name: String?,
    override val content: String?,
    override var date: String,
    override val type: ChatFileType,
    override val fileUrl: String?,
    override val id: Long,
    override val readCount: Int,
    override var showMinute: Boolean = true,
) : ChatMessage