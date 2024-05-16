package com.minhoi.memento.repository.chat

import com.minhoi.memento.data.dto.chat.AllChatMessageResponse
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatRooms(memberId: Long): Flow<ApiResult<List<ChatRoom>>>

    fun getRoomId(receiverId: Long): Flow<ApiResult<ChatRoom>>

    fun getMessages(roomId: Long, page: Int, size: Int): Flow<ApiResult<AllChatMessageResponse>>
}