package com.minhoi.memento.data.model

enum class ChatFileType {
    MESSAGE,
    IMAGE,
    VIDEO,
    ZIP,
    PDF,
    CONTACT;

    companion object {
        fun toFileType(type: String): ChatFileType? {
            return when (type) {
                "MESSAGE" -> MESSAGE
                "IMAGE" -> IMAGE
                "VIDEO" -> VIDEO
                "ZIP" -> ZIP
                "PDF" -> PDF
                "CONTACT" -> CONTACT
                else -> null
            }
        }
    }
}
