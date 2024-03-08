package com.minhoi.memento.data.network.service

import com.minhoi.memento.data.dto.chat.ChatRoom
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatService {
    @GET("api/chat/chatRooms")
    suspend fun getChatRooms(@Path("memberId") memberId: Long): Response<List<ChatRoom>>
}