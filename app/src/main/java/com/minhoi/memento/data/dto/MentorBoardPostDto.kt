package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.utils.BoardType
import com.minhoi.memento.utils.DayOfWeek

data class MentorBoardPostDto (
    @SerializedName("title")
    val title: String,
    val content: String,
    val consultTime: Int,
    val boardType: BoardType,
    val times: List<TimeTableDto>,
    val availableDays: List<DayOfWeek>
)

