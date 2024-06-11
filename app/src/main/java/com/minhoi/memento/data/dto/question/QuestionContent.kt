package com.minhoi.memento.data.dto.question

import com.google.gson.annotations.SerializedName

data class QuestionContent(
    @SerializedName("subBoardId") val questionId: Long,
    val title: String,
    val content: String,
    val year: String,
    val schoolName: String,
    val majorName: String,
    val memberId: String,
    val memberName: String,
    val writeTime: String,
    val likeCount: Int,
    val replyCount: Int,
    val like: Boolean
)
