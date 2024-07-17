package com.minhoi.memento.data.dto.question

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.dto.paging.PageInfo

data class QuestionListResponse(
    @SerializedName("data") val content: List<QuestionContent>,
    @SerializedName("pageInfo") val pageInfo: PageInfo
)
