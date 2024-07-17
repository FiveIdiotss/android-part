package com.minhoi.memento.data.dto.notification

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.dto.paging.PageInfo

data class NotificationListResponse(
    @SerializedName("data") val content: List<NotificationListDto>,
    @SerializedName("pageInfo") val pageInfo: PageInfo
)
