package com.minhoi.memento.repository.question

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.question.QuestionListResponse
import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.QuestionResponse
import com.minhoi.memento.data.dto.question.ReplyListResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getQuestions(page: Int,
                     size: Int,
                     schoolFilter: Boolean?,
                     boardCategory: String?,
                     searchKeyWord: String?
    ): Flow<ApiResult<CommonResponse<QuestionListResponse>>>

    fun getQuestion(questionId: Long): Flow<ApiResult<CommonResponse<QuestionResponse>>>

    fun postQuestion(question: QuestionPostRequest): Flow<ApiResult<CommonResponse<String>>>

    fun postReply(questionId: Long, content: String): Flow<ApiResult<CommonResponse<String>>>

    fun getReplies(page: Int, size: Int, questionId: Long): Flow<ApiResult<CommonResponse<ReplyListResponse>>>

    fun getMyQuestion(page: Int, size: Int, memberId: Long): Flow<ApiResult<CommonResponse<QuestionListResponse>>>
}