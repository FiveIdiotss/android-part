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

    @SerializedName("introduce")
    val introduction: String,

    @SerializedName("target")
    val target: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("memberId")
    val memberId: Long,

    @SerializedName("memberImageUrl")
    val memberImageUrl: String,

    @SerializedName("representImage")
    val representImageUrl: String,

    @SerializedName("favorite")
    var isBookmarked: Boolean
)
