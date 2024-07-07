package com.minhoi.memento.repository.chat

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.model.MentoringExtendStatus
import com.minhoi.memento.data.model.safeFlow
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.ChatService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService
) : ChatRepository {

    override fun getChatRooms(): Flow<ApiResult<CommonResponse<List<ChatRoom>>>> = safeFlow {
        chatService.getChatRooms()
    }

    override fun getChatRoom(roomId: Long): Flow<ApiResult<CommonResponse<ChatRoom>>> = safeFlow {
        chatService.getChatRoom(roomId)
    }

    override fun getMessages(roomId: Long, page: Int, size: Int) = safeFlow {
        chatService.getChatMessages(roomId, page, size)
    }

    override fun sendFile(file: MultipartBody.Part, roomId: Long) = safeFlow {
        chatService.sendFile(file, roomId)
    }

    override fun extendMentoringTime(roomId: Long): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        chatService.requestExtendMentoringTime(roomId)
    }

    override fun processExtendMentoringTime(
        chatId: Long,
        status: MentoringExtendStatus,
    ): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        chatService.processExtendMentoringTime(chatId, status)
    }
}