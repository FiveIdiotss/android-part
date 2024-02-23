package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class MentoringApplyDto(
    @SerializedName("boardTitle")
    val boardTitle: String,
    @SerializedName("applyId")
    val applyId: Long,
    @SerializedName("boardId")
    val boardId: Long,
    @SerializedName("content")
    val content: String,
    @SerializedName("applyState")
    val applyState: String
)
