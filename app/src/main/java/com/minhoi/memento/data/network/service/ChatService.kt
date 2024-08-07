package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.chat.AllChatMessageResponse
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.model.MentoringExtendStatus
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatService {
    @GET("api/chat/chatRooms")
    suspend fun getChatRooms(): Response<CommonResponse<List<ChatRoom>>>

    @GET("api/chat/chatRoom")
    suspend fun getChatRoom(@Query("chatRoomId") roomId: Long): Response<CommonResponse<ChatRoom>>

    @GET("api/chat/messages/{chatRoomId}")
    suspend fun getChatMessages(@Path("chatRoomId") roomId: Long, @Query("page") page: Int, @Query("size") size: Int): Response<CommonResponse<AllChatMessageResponse>>

    @Multipart
    @POST("api/chat/sendFile")
    suspend fun sendFile(
        @Part file: MultipartBody.Part,
        @Query("chatRoomId") roomId: Long
    ): Response<CommonResponse<String>>

    @POST("api/chat/extend/request/{chatRoomId}")
    suspend fun requestExtendMentoringTime(@Path("chatRoomId") roomId: Long): Response<CommonResponse<String>>

    @POST("api/chat/extend/{chatId}")
    suspend fun processExtendMentoringTime(@Path("chatId") chatId: Long, @Query("status") status: MentoringExtendStatus): Response<CommonResponse<String>>
}