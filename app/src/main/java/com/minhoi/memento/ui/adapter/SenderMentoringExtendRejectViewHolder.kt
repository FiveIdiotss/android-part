package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.SenderExtendRequestRejectRowItemBinding

class SenderMentoringExtendRejectViewHolder(private val binding: SenderExtendRequestRejectRowItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Sender) {
        binding.senderData = item
    }
    }