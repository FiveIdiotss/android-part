package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName

data class ChatRoom(
    @SerializedName("chatRoomId")
    val id: Long,
    @SerializedName("receiverId")
    val receiverId: Long,
    @SerializedName("receiverName")
    val receiverName: String,
    @SerializedName("latestMessageDTO")
    val latestMessage: LatestMessageDto
)
