package com.minhoi.memento.data.network.service

import com.minhoi.memento.data.dto.chat.ChatRoom
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatService {
    @GET("api/chat/chatRooms")
    suspend fun getChatRooms(@Query("memberId") memberId: Long): Response<List<ChatRoom>>

    @GET("api/chat/chatRoom")
    suspend fun getRoomId(@Query("receiverId") receiverId: Long): Response<ChatRoom>
}