package com.minhoi.memento.repository.question

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.question.QuestionListResponse
import com.minhoi.memento.data.dto.question.ReplyRequest
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.QuestionService
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionService: QuestionService,
) : QuestionRepository {

    override fun getQuestions(
        page: Int,
        size: Int,
        schoolFilter: Boolean,
        likeFilter: Boolean,
        boardCategory: String?,
        searchKeyWord: String?
    ): Flow<ApiResult<CommonResponse<QuestionListResponse>>> = safeFlow {
        questionService.getQuestions(
            page,
            size,
            schoolFilter = schoolFilter,
            likeFilter = likeFilter,
            boardCategory = boardCategory,
            searchKeyWord = searchKeyWord,
            "QUEST"
        )
    }

    override fun getQuestion(questionId: Long) = safeFlow {
        questionService.getQuestion(questionId)
    }

    override fun postQuestion(
        question: RequestBody,
        images: List<MultipartBody.Part>
    ): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        questionService.postQuestion(question, images)
    }

    override fun postReply(questionId: Long, content: String) = safeFlow {
        questionService.postReply(questionId, ReplyRequest(content))
    }

    override fun getReplies(page: Int, size: Int, questionId: Long) = safeFlow {
        questionService.getReplies(questionId, page, true, size)
    }

    override fun getMyQuestions(
        page: Int,
        size: Int,
        memberId: Long,
    ): Flow<ApiResult<CommonResponse<QuestionListResponse>>> = safeFlow {
        questionService.getMyQuestions(memberId, page, size, "QUEST")
    }

    override fun executeLike(questionId: Long): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        questionService.executeLike(questionId)
    }
    override fun unExecuteLike(questionId: Long): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        questionService.unExecuteLike(questionId)
    }
}