package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.safeFlow
import com.minhoi.memento.data.network.service.ChatService
import kotlinx.coroutines.flow.Flow

class ChatRepository {
    private val chatService = RetrofitClient.getLoggedInInstance().create(ChatService::class.java)

    suspend fun getChatRooms(memberId: Long): Flow<ApiResult<List<ChatRoom>>> = safeFlow {
        chatService.getChatRooms(memberId)
    }

    suspend fun getRoomId(receiverId: Long): Flow<ApiResult<ChatRoom>> = safeFlow {
        chatService.getRoomId(receiverId)
    }
}