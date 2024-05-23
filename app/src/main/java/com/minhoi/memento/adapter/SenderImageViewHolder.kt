package com.minhoi.memento.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.SenderImageRowItemBinding

class SenderImageViewHolder(private val binding: SenderImageRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Sender) {
        Log.d("SenderViewHolder", "bind: $item")
        binding.senderData = item
    }
}