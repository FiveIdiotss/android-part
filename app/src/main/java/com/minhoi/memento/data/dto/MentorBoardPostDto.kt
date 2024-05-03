package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.model.DayOfWeek

data class MentorBoardPostDto (
    @SerializedName("title")
    val title: String,
    @SerializedName("introduce")
    val introduction: String,
    @SerializedName("target")
    val target: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("consultTime")
    val consultTime: Int,
    @SerializedName("boardType")
    val boardType: BoardType,
    @SerializedName("times")
    val times: List<TimeTableDto>,
    @SerializedName("availableDays")
    val availableDays: List<DayOfWeek>
)

