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
    val latestMessage: LatestMessageDto,
    val receiverImageUrl: String,
    val boardTitle: String,
    @SerializedName("date")
    val startDate: String,
    val startTime: String,
    val unreadMessageCount: Int
)
