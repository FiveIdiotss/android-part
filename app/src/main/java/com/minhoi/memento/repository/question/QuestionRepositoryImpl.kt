package com.minhoi.memento.repository.question

import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.ReplyRequest
import com.minhoi.memento.data.network.service.QuestionService
import com.minhoi.memento.utils.safeFlow
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionService: QuestionService,
) : QuestionRepository {

    override fun getQuestions(page: Int, size: Int) = safeFlow {
        questionService.getQuestions(page, size)
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
        questionService.getReplies(questionId, page, size)
    }
}