package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverMentorExtendAcceptRowItemBinding

class ReceiverMentoringExtendAcceptViewHolder(private val binding: ReceiverMentorExtendAcceptRowItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Receiver) {
        binding.receiverData = item
    }
    }