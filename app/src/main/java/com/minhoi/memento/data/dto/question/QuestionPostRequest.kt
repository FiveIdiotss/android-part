package com.minhoi.memento.data.dto.question

data class QuestionPostRequest(
    val title: String,
    val content: String,
    val boardCategory: String,
    val subBoardType: String,
    val platform: String = "APP"
)
