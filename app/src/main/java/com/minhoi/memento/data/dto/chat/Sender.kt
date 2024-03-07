package com.minhoi.memento.data.dto.chat

data class Sender(
    override val content: String,
    override val date: String,
    val senderEmail: String
) : ChatMessage
