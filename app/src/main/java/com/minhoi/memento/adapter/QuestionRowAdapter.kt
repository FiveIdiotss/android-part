package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.QuestionResponse
import com.minhoi.memento.databinding.QuestionBoardRowItemBinding

class QuestionRowAdapter : ListAdapter<QuestionResponse, QuestionRowAdapter.QuestionRowViewHolder>(DiffCallback()) {

    inner class QuestionRowViewHolder(private val binding: QuestionBoardRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionResponse) {
            binding.questionData = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionRowViewHolder {
        val binding = QuestionBoardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionRowViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<QuestionResponse>() {
        override fun areItemsTheSame(oldItem: QuestionResponse, newItem: QuestionResponse): Boolean {
            return oldItem.questionId == newItem.questionId
        }

        override fun areContentsTheSame(oldItem: QuestionResponse, newItem: QuestionResponse): Boolean {
            return oldItem == newItem
        }
    }
}



