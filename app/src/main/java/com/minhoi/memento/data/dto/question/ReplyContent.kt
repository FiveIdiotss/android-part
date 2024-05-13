package com.minhoi.memento.data.dto.question

import com.google.gson.annotations.SerializedName

data class ReplyContent(
    val replyId: Long,
    val memberId: Long,
    val memberName: String,
    val majorName: String,
    @SerializedName("localDateTime") val postDate: String,
    val content: String,
    @SerializedName("imageUrl") val profileImageUrl: String
)
