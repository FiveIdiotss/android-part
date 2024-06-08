package com.minhoi.memento.data.network.service

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.question.QuestionListResponse
import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.QuestionResponse
import com.minhoi.memento.data.dto.question.ReplyListResponse
import com.minhoi.memento.data.dto.question.ReplyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface QuestionService {
    @GET("api/pageSubBoards")
    suspend fun getQuestions(@Query("page") page: Int, @Query("size") size: Int): Response<CommonResponse<QuestionListResponse>>

    @GET("api/subBoard/{subBoardId}")
    suspend fun getQuestion(@Path("subBoardId") questionId: Long): Response<CommonResponse<QuestionResponse>>

    @POST("api/subBoard")
    suspend fun postQuestion(@Body question: QuestionPostRequest): Response<CommonResponse<String>>

    @GET("api/reply/{subBoardId}")
    suspend fun getReplies(
        @Path("subBoardId") questionId: Long,
        @Query("page") page: Int,
        @Query("isRecent") isRecent: Boolean,
        @Query("size") size: Int,
    ): Response<CommonResponse<ReplyListResponse>>

    @POST("api/reply/{subBoardId}")
    suspend fun postReply(@Path("subBoardId") questionId: Long, @Body content: ReplyRequest): Response<CommonResponse<String>>
}