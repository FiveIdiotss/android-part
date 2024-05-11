package com.minhoi.memento.data.network.service

import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.data.dto.question.QuestionListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionService {
    @GET("api/pageSubBoards")
    suspend fun getQuestions(@Query("page") page: Int, @Query("size") size: Int): Response<QuestionListResponse>

    @GET("api/subBoard/{subBoardId}")
    suspend fun getQuestion(@Path("subBoardId") questionId: Long): Response<QuestionContent>
}