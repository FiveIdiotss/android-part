package com.minhoi.memento.data.dto.chat

interface ChatMessage {
    val id: Long
    val content: String
    val date: String
    val name: String
}