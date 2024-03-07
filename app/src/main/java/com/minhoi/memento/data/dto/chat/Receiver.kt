package com.minhoi.memento.data.dto.chat

data class Receiver(
    override val date: String,
    override val content: String,
    val receiverEmail: String
) : ChatMessage
