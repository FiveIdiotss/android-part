package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.BoardType

data class BoardWriteDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("boardType")
    val boardType: BoardType
)
