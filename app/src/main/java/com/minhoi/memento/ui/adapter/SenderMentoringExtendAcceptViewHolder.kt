package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.SenderMentorExtendAcceptRowItemBinding

class SenderMentoringExtendAcceptViewHolder(private val binding: SenderMentorExtendAcceptRowItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Sender) {
        binding.senderData = item
    }
}