package com.minhoi.memento.data.dto.board

import com.google.gson.annotations.SerializedName
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
    @SerializedName("boardCategory")
    val boardType: String,
    @SerializedName("times")
    val times: List<TimeTableDto>,
    @SerializedName("availableDays")
    val availableDays: List<DayOfWeek>,
    val platform: String = "APP"
)

