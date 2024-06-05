package com.minhoi.memento.data.dto.notification

import com.google.gson.annotations.SerializedName
import com.minhoi.memento.data.model.NotificationListType

data class NotificationListDto(
    @SerializedName("notificationId") val id: Long,
    val senderId: String,
    val senderName: String,
    val senderImageUrl: String,
    val otherPK: Long,
    val title: String,
    val content: String,
    @SerializedName("notificationType") val type: NotificationListType,
    val arriveTime: String
)
