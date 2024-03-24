package com.minhoi.memento.data.dto.chat

data class Sender(
    override val id: Long,
    override val name: String?,
    override val content: String,
    override var date: String,
    override var showDate: Boolean = true
) : ChatMessage
