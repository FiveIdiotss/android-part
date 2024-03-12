package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName

data class ChatRoom(
    @SerializedName("chatRoomId")
    val id: Long,
    @SerializedName("recevierId")
    val receiverId: Long,
    @SerializedName("recipientName")
    val receiverName: String
)
