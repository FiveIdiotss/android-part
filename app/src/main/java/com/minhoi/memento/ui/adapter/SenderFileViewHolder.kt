package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.SenderFileRowItemBinding

class SenderFileViewHolder(private val binding: SenderFileRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Sender) {
        binding.senderData = item
    }
}