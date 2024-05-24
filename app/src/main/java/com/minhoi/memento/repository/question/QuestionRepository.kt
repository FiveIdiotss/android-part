package com.minhoi.memento.repository.question

import com.minhoi.memento.data.dto.question.QuestionListResponse
import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.QuestionResponse
import com.minhoi.memento.data.dto.question.ReplyListResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getQuestions(page: Int, size: Int): Flow<ApiResult<QuestionListResponse>>

    fun getQuestion(questionId: Long): Flow<ApiResult<QuestionResponse>>

    fun postQuestion(question: QuestionPostRequest): Flow<ApiResult<String>>

    fun postReply(questionId: Long, content: String): Flow<ApiResult<String>>

    fun getReplies(page: Int, size: Int, questionId: Long): Flow<ApiResult<ReplyListResponse>>
}