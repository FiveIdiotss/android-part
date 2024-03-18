package com.minhoi.memento.data.dto.chat

data class Sender(
    override val id: Long,
    override val name: String,
    override val content: String,
    override val date: String
) : ChatMessage
