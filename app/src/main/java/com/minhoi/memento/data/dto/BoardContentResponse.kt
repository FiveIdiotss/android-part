package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.utils.DayOfWeek

data class BoardContentResponse(
    @SerializedName("boardDTO")
    val boardDTO: BoardContentDto,
    @SerializedName("consultTime")
    val consultTime: Int,
    @SerializedName("times")
    val timeTable: List<TimeTableDto>,
    @SerializedName("availableDays")
    val availableDays: List<DayOfWeek>
)
