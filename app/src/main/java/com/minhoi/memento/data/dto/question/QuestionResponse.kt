package com.minhoi.memento.data.dto.question

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("subBoardId") val questionId: String,
    val title: String,
    val content: String,
    val year: String,
    val schoolName: String,
    val majorName: String,
    val memberId: String,
    val memberName: String,
    val writeTime: String
)
