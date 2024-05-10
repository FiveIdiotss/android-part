package com.minhoi.memento.repository

import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.QuestionService
import com.minhoi.memento.utils.safeFlow

class QuestionRepository {
    private val questionService = RetrofitClient.getLoggedInInstance().create(QuestionService::class.java)

    fun getQuestions(page: Int, size: Int) = safeFlow {
        questionService.getQuestions(page, size)
    }
}