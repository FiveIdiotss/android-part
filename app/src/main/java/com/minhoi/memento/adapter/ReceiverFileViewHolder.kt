package com.minhoi.memento.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverFileRowItemBinding

class ReceiverFileViewHolder(private val binding: ReceiverFileRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Receiver) {
        binding.receiverData = item
    }
}