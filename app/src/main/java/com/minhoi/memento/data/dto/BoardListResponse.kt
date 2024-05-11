package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class BoardListResponse(
    @SerializedName("data") val content: List<BoardContentDto>,
    @SerializedName("pageInfo") val pageInfo: PageInfo
)
