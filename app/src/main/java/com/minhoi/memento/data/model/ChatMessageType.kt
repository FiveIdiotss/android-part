package com.minhoi.memento.data.model

enum class ChatMessageType {
    TEXT,
    IMAGE,
    VIDEO,
    ZIP,
    PDF,
    CONTACT,
    FILE,
    CONSULT_EXTEND,
    CONSULT_EXTEND_ACCEPT,
    CONSULT_EXTEND_DECLINE,
    CONSULT_EXTEND_COMPLETE;

    companion object {
        fun toMessageType(type: String): ChatMessageType {
            return when (type) {
                "MESSAGE" -> TEXT
                "IMAGE" -> IMAGE
                "VIDEO" -> VIDEO
                "ZIP" -> ZIP
                "PDF" -> PDF
                "CONTACT" -> CONTACT
                "CONSULT_EXTEND" -> CONSULT_EXTEND
                else -> FILE
            }
        }
    }
}
