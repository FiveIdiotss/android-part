package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.ReplyContent
import com.minhoi.memento.databinding.ReplyRowItemBinding

class ReplyAdapter : PagingDataAdapter<ReplyContent, ReplyAdapter.ReplyViewHolder>(DiffCallback()) {

    inner class ReplyViewHolder(private val binding: ReplyRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReplyContent) {
            binding.reply = item
        }
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val binding = ReplyRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReplyViewHolder(binding)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ReplyContent>() {
        override fun areItemsTheSame(oldItem: ReplyContent, newItem: ReplyContent): Boolean {
            return oldItem.replyId == newItem.replyId
        }

        override fun areContentsTheSame(oldItem: ReplyContent, newItem: ReplyContent): Boolean {
            return oldItem == newItem
        }

    }
}