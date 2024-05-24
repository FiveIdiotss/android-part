package com.minhoi.memento.data.dto.question

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.dto.PageInfo

data class ReplyListResponse(
    @SerializedName("data") val data: List<ReplyContent>,
    val pageInfo: PageInfo
)
