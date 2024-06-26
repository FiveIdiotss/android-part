package com.minhoi.memento.repository.question

import com.minhoi.memento.base.CommonResponse
import com.minhoi.memento.data.dto.question.QuestionListResponse
import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.ReplyRequest
import com.minhoi.memento.data.network.ApiResult
import com.minhoi.memento.data.network.service.QuestionService
import com.minhoi.memento.utils.safeFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionService: QuestionService,
) : QuestionRepository {

    override fun getQuestions(
        page: Int,
        size: Int,
        schoolFilter: Boolean,
        boardCategory: String?,
        searchKeyWord: String?
    ): Flow<ApiResult<CommonResponse<QuestionListResponse>>> = safeFlow {
        questionService.getQuestions(
            page,
            size,
            schoolFilter = schoolFilter,
            boardCategory = boardCategory,
            searchKeyWord = searchKeyWord,
            "QUEST"
        )
    }

    override fun getQuestion(questionId: Long) = safeFlow {
        questionService.getQuestion(questionId)
    }

    override fun postQuestion(question: QuestionPostRequest) = safeFlow {
        questionService.postQuestion(question)
    }

    override fun postReply(questionId: Long, content: String) = safeFlow {
        questionService.postReply(questionId, ReplyRequest(content))
    }

    override fun getReplies(page: Int, size: Int, questionId: Long) = safeFlow {
        questionService.getReplies(questionId, page, true, size)
    }

    override fun getMyQuestion(
        page: Int,
        size: Int,
        memberId: Long,
    ): Flow<ApiResult<CommonResponse<QuestionListResponse>>> {
        TODO("Not yet implemented")
    }

    override fun executeLike(questionId: Long): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        questionService.executeLike(questionId)
    }
    override fun unExecuteLike(questionId: Long): Flow<ApiResult<CommonResponse<String>>> = safeFlow {
        questionService.unExecuteLike(questionId)
    }
}