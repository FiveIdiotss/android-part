package com.minhoi.memento.repository.question

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.question.QuestionListResponse
import com.minhoi.memento.data.dto.question.QuestionResponse
import com.minhoi.memento.data.dto.question.ReplyListResponse
import com.minhoi.memento.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface QuestionRepository {
    fun getQuestions(page: Int,
                     size: Int,
                     schoolFilter: Boolean,
                     likeFilter: Boolean,
                     boardCategory: String?,
                     searchKeyWord: String?
    ): Flow<ApiResult<CommonResponse<QuestionListResponse>>>

    fun getQuestion(questionId: Long): Flow<ApiResult<CommonResponse<QuestionResponse>>>

    fun postQuestion(question: RequestBody, images: List<MultipartBody.Part>): Flow<ApiResult<CommonResponse<String>>>

    fun postReply(questionId: Long, content: String): Flow<ApiResult<CommonResponse<String>>>

    fun getReplies(page: Int, size: Int, questionId: Long): Flow<ApiResult<CommonResponse<ReplyListResponse>>>

    fun getMyQuestions(page: Int, size: Int, memberId: Long): Flow<ApiResult<CommonResponse<QuestionListResponse>>>

    fun executeLike(questionId: Long): Flow<ApiResult<CommonResponse<String>>>

    fun unExecuteLike(questionId: Long): Flow<ApiResult<CommonResponse<String>>>
}