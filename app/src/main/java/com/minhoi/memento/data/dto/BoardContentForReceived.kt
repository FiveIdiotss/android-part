package com.minhoi.memento.data.dto

data class BoardContentForReceived(
    val boardId: Long,
    val memberName: String,
    val title: String,
    val school: String,
    val major: String,
    val year: Int,
    val introduction: String,
    val target: String,
    val content: String,
    val memberId: Long,
    var isBookmarked: Boolean = false,
    var isExpanded: Boolean = false,
)
