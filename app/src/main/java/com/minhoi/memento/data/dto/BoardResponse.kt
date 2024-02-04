package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class BoardResponse(
    @SerializedName("size") val size: Int,
    @SerializedName("content") val content: List<BoardContentDto>,
    @SerializedName("number") val number: Int,
    @SerializedName("sort") val sort: Sort,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("pageable") val pageable: Pageable,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean,
    @SerializedName("empty") val empty: Boolean
)
