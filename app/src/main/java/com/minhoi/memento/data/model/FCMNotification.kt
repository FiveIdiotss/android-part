package com.minhoi.memento.data.model

data class FCMNotification(
    val senderId: Long,
    val otherPK: Long,
    val senderName: String,
    val content: String,
    val profileUri: String
)
