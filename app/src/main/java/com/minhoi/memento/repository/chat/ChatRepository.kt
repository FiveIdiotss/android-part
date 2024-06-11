package com.minhoi.memento.repository.chat

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.chat.AllChatMessageResponse
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.dto.chat.FileUploadResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface ChatRepository {
    fun getChatRooms(memberId: Long): Flow<ApiResult<CommonResponse<List<ChatRoom>>>>

    fun getRoomId(receiverId: Long): Flow<ApiResult<CommonResponse<ChatRoom>>>

    fun getMessages(roomId: Long, page: Int, size: Int): Flow<ApiResult<CommonResponse<AllChatMessageResponse>>>

    fun sendFile(file: MultipartBody.Part, roomId: Long): Flow<ApiResult<CommonResponse<FileUploadResponse>>>
}