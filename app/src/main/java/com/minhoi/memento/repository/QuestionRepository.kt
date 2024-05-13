package com.minhoi.memento.repository

import com.minhoi.memento.data.dto.question.QuestionPostRequest
import com.minhoi.memento.data.dto.question.ReplyRequest
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.QuestionService
import com.minhoi.memento.utils.safeFlow

class QuestionRepository {
    private val questionService = RetrofitClient.getLoggedInInstance().create(QuestionService::class.java)

    fun getQuestions(page: Int, size: Int) = safeFlow {
        questionService.getQuestions(page, size)
    }

    fun getQuestion(questionId: Long) = safeFlow {
        questionService.getQuestion(questionId)
    }

    fun postQuestion(question: QuestionPostRequest) = safeFlow {
        questionService.postQuestion(question)
    }
    fun postReply(questionId: Long, content: String) = safeFlow {
        questionService.postReply(questionId, ReplyRequest(content))
    }

    fun getReplies(page: Int, size: Int, questionId: Long) = safeFlow {
        questionService.getReplies(questionId, page, size)
    }
}