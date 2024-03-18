package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.dto.Pageable
import com.minhoi.memento.data.dto.Sort

data class AllChatMessageResponse(
    @SerializedName("size") val size: Int,
    @SerializedName("content") val content: List<MessageDto>,
    @SerializedName("number") val number: Int,
    @SerializedName("sort") val sort: Sort,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("pageable") val pageable: Pageable,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean,
    @SerializedName("empty") val empty: Boolean
)
