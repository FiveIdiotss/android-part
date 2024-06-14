package com.minhoi.memento.data.dto.question

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("subBoardDTO") val questionContent: QuestionContent,
    @SerializedName("subBoardImageUrls") val questionImageUrls: List<QuestionImage>
)
