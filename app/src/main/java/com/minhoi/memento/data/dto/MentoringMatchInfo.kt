package com.minhoi.memento.data.dto

import com.google.gson.annotations.SerializedName

data class MentoringMatchInfo(
    @SerializedName("matchingId") val matchingId: Long,
    @SerializedName("applyId") val applyId: Long,
    @SerializedName("otherMemberId") val otherMemberId: Long,
    @SerializedName("otherMemberName") val otherMemberName: String,
    @SerializedName("date") val date: String,
    @SerializedName("startTime") val startTime: String
)
