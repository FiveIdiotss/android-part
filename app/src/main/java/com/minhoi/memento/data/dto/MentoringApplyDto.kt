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
    val applyState: String,
    @SerializedName("memberId")
    val memberId: Long,
    @SerializedName("date")
    val startDate: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("memberName")
    val memberName: String,
    @SerializedName("schoolName")
    val schoolName: String,
    @SerializedName("majorName")
    val majorName: String,
    @SerializedName("memberImageUrl")
    val memberImageUrl: String
)
