package com.minhoi.memento.data.model

enum class NotificationListType(val message: String) {
    REPLY_QUEST("%s 질문글에 댓글이 추가되었습니다."),
    APPLY("%s 님이 %s 멘토링 모집글에 멘토링을 요청하였습니다."),
    MATCHING_COMPLETE("멘토링 매칭성공"),
    MATCHING_DECLINE("멘토링 매칭실패")
}