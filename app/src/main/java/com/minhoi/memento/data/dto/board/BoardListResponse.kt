package com.minhoi.memento.data.dto.board

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.dto.paging.PageInfo

data class BoardListResponse(
    @SerializedName("data") val content: List<BoardContentDto>,
    @SerializedName("pageInfo") val pageInfo: PageInfo
)
