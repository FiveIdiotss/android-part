package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.SenderExtendRequestRowItemBinding

class SenderMentoringExtendRequestViewHolder(private val binding: SenderExtendRequestRowItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Sender) {
        binding.senderData = item
    }
}