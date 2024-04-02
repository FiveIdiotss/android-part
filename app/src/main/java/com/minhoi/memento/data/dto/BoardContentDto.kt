package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class BoardContentDto(
    @SerializedName("boardId")
    val boardId: Long,

    @SerializedName("memberName")
    val memberName: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("schoolName")
    val school: String,

    @SerializedName("majorName")
    val major: String,

    @SerializedName("year")
    val year: Int,

    @SerializedName("content")
    val content: String,

    @SerializedName("memberId")
    val memberId: Long,

    var isBookmarked: Boolean = false
)
