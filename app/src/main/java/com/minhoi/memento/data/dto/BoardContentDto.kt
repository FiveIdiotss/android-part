package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.BoardType

data class BoardContentDto(
    @SerializedName("boardId")
    val boardId: Long,

    @SerializedName("boardType")
    val boardType: BoardType,

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

    @SerializedName("introduce")
    val introduction: String,

    @SerializedName("target")
    val target: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("memberId")
    val memberId: Long,

    var isBookmarked: Boolean = false
)
