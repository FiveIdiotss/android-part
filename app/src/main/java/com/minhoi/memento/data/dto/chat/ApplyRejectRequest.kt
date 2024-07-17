package com.minhoi.memento.data.dto.chat

import com.google.gson.annotations.SerializedName

data class ApplyRejectRequest(
    val applyId: Long,
    val reason: RejectReason
) {
    data class RejectReason(
        @SerializedName("content")
        val content: String
    )
}
