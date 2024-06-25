package com.minhoi.memento.data.model

data class ChatNotification(
    val senderId: Long,
    val senderName: String,
    val content: String,
    val profileUri: String
)
