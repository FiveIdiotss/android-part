package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.question.QuestionListResponse
import com.minhoi.memento.data.dto.question.QuestionResponse
import com.minhoi.memento.data.dto.question.ReplyListResponse
import com.minhoi.memento.data.dto.question.ReplyRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface QuestionService {
    @GET("api/subBoards")
    suspend fun getQuestions(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("schoolFilter") schoolFilter: Boolean,
        @Query("favoriteFilter") likeFilter: Boolean,
        @Query("boardCategory") boardCategory: String?,
        @Query("keyWord") searchKeyWord: String?,
        @Query("subBoardType") type: String,
    ): Response<CommonResponse<QuestionListResponse>>

    @GET("api/subBoard/{subBoardId}")
    suspend fun getQuestion(@Path("subBoardId") questionId: Long): Response<CommonResponse<QuestionResponse>>

    @POST("api/android/subBoard")
    @Multipart
    suspend fun postQuestion(
        @Part("request") question: RequestBody,
        @Part images: List<MultipartBody.Part>? = null // 이미지 리스트가 없을 때는 null 처리
    ): Response<CommonResponse<String>>

    @GET("api/reply/{subBoardId}")
    suspend fun getReplies(
        @Path("subBoardId") questionId: Long,
        @Query("page") page: Int,
        @Query("isRecent") isRecent: Boolean,
        @Query("size") size: Int,
    ): Response<CommonResponse<ReplyListResponse>>

    @POST("api/reply/{subBoardId}")
    suspend fun postReply(@Path("subBoardId") questionId: Long, @Body content: ReplyRequest): Response<CommonResponse<String>>

    @POST("api/like/{subBoardId}")
    suspend fun executeLike(@Path("subBoardId") questionId: Long): Response<CommonResponse<String>>

    @DELETE("api/like/{subBoardId}")
    suspend fun unExecuteLike(@Path("subBoardId") questionId: Long): Response<CommonResponse<String>>

    @GET("api/subBoards/{memberId}")
    suspend fun getMyQuestions(
        @Path("memberId") memberId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("subBoardType") subBoardType: String
    ): Response<CommonResponse<QuestionListResponse>>
}