package com.minhoi.memento.data.dto.chat

interface ChatMessage {
    val id: Long
    val content: String?
    var date: String
    val name: String?
    var showMinute: Boolean
    val image: String?
}