package com.minhoi.memento.data.model

data class ChatNotification(
    val roomId: Long,
    val senderName: String,
    val content: String,
    val profileUri: String
)
