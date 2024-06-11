package com.minhoi.memento.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@kotlinx.parcelize.Parcelize
data class MentoringReceivedDto(
    @SerializedName("applyId")
    val applyId: Long,

    @SerializedName("boardId")
    val boardId: Long,

    @SerializedName("boardTitle")
    val boardTitle: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("applyState")
    val applyState: String,

    @SerializedName("otherMemberId")
    val otherMemberId: Long,

    @SerializedName("otherMemberName")
    val otherMemberName: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("startTime")
    val startTime: String
) : Parcelable
