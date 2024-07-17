package com.minhoi.memento.data.dto.board

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.DayOfWeek

data class BoardContentResponse(
    @SerializedName("boardDTO")
    val boardDTO: BoardContentDto,
    @SerializedName("consultTime")
    val consultTime: Int,
    @SerializedName("times")
    val timeTable: List<TimeTableDto>,
    @SerializedName("availableDays")
    val availableDays: List<DayOfWeek>,
    val boardImageUrls: List<BoardImage>
)
