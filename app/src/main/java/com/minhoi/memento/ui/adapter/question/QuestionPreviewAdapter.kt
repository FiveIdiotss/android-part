package com.minhoi.memento.ui.adapter.question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.databinding.QuestionPreviewColumnItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class QuestionPreviewAdapter(private val onClickListener: (Long) -> Unit) : ListAdapter<QuestionContent, QuestionPreviewAdapter.QuestionPreviewViewHolder>(
    QuestionDiffCallback()
){
    inner class QuestionPreviewViewHolder(private val binding: QuestionPreviewColumnItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionContent) {
            binding.root.setOnSingleClickListener {
                onClickListener(item.questionId)
            }
            binding.question = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionPreviewViewHolder {
        val binding = QuestionPreviewColumnItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionPreviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionPreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
