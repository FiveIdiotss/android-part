package com.minhoi.memento.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverImageRowItemBinding

class ReceiverImageViewHolder(private val binding: ReceiverImageRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Receiver) {
        binding.receiverData = item
    }
}