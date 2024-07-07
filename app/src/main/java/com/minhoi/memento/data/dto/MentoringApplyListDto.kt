package com.minhoi.memento.data.dto

import com.minhoi.memento.data.model.ApplyStatus

data class MentoringApplyListDto(
    val applyId: Long,
    val boardId: Long,
    val boardTitle: String,
    val content: String,
    val applyState: ApplyStatus,
    val otherMemberId: Long,
    val otherMemberName: String,
    val applyContent: String?,
    val date: String,
    val startTime: String
)
