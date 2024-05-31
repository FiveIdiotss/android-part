package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.databinding.QuestionPreviewColumnItemBinding

class QuestionPreviewAdapter : ListAdapter<QuestionContent, QuestionPreviewAdapter.QuestionPreviewViewHolder>(
    QuestionDiffCallback()
){
    inner class QuestionPreviewViewHolder(private val binding: QuestionPreviewColumnItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionContent) {
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
