package com.minhoi.memento.ui.adapter.question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.QuestionImage
import com.minhoi.memento.databinding.QuestionInfoImageLayoutBinding

class QuestionInfoImageAdapter : ListAdapter<QuestionImage, QuestionInfoImageAdapter.ViewHolder>(
    DiffCallback()
) {

    inner class ViewHolder(private val binding: QuestionInfoImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionImage) {
            binding.questionImage = item.imageUrl
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuestionInfoImageLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<QuestionImage>() {
        override fun areItemsTheSame(oldItem: QuestionImage, newItem: QuestionImage): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: QuestionImage, newItem: QuestionImage): Boolean {
            return oldItem == newItem
        }
    }
}


