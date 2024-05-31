package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.minhoi.memento.data.dto.question.QuestionContent

class QuestionDiffCallback : DiffUtil.ItemCallback<QuestionContent>() {
    override fun areItemsTheSame(oldItem: QuestionContent, newItem: QuestionContent): Boolean {
        return oldItem.questionId == newItem.questionId
    }

    override fun areContentsTheSame(oldItem: QuestionContent, newItem: QuestionContent): Boolean {
        return oldItem == newItem
    }
}