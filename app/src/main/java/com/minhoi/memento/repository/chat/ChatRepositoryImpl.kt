package com.minhoi.memento.repository.chat

import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.utils.safeFlow
import com.minhoi.memento.data.network.service.ChatService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor() : ChatRepository {
    private val chatService = RetrofitClient.getLoggedInInstance().create(ChatService::class.java)

    override fun getChatRooms(memberId: Long): Flow<ApiResult<List<ChatRoom>>> = safeFlow {
        chatService.getChatRooms(memberId)
    }

    override fun getRoomId(receiverId: Long): Flow<ApiResult<ChatRoom>> = safeFlow {
        chatService.getRoomId(receiverId)
    }

    override fun getMessages(roomId: Long, page: Int, size: Int) = safeFlow {
        chatService.getChatMessages(roomId, page, size)
    }
}