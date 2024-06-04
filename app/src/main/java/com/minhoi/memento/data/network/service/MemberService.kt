package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MemberDTO
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.notification.NotificationListResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MemberService {
    @GET("api/member/{memberId}")
    suspend fun getMemberInfo(@Path("memberId") memberId: Long): Response<CommonResponse<MemberDTO>>

    @Multipart
    @POST("api/member/image")
    suspend fun uploadProfileImage(@Part image: MultipartBody.Part): Response<CommonResponse<String>>

    @POST("api/member/defaultImage")
    suspend fun setDefaultProfileImage(): Response<CommonResponse<String>>

    @GET("api/boards/favorites")
    suspend fun getBookmarkBoards(): Response<CommonResponse<List<BoardContentDto>>>

    @GET("api/memberBoards/{memberId}")
    suspend fun getMemberBoards(@Path("memberId") memberId: Long, @Query("page") page: Int, @Query("size") size: Int): Response<CommonResponse<BoardListResponse>>

    @GET("api/fcm")
    suspend fun getNotificationList(@Query("page") page: Int, @Query("size") size: Int): Response<CommonResponse<NotificationListResponse>>

    @GET("api/apply/{applyId}")
    suspend fun getApplyInfo(@Path("applyId") applyId: Long): Response<CommonResponse<MentoringApplyDto>>
}