package com.minhoi.memento.ui.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.SenderImageRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class SenderImageViewHolder(
    private val binding: SenderImageRowItemBinding,
    private val onImageClickListener: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Sender) {
        Log.d("SenderViewHolder", "bind: $item")
        binding.senderData = item
        binding.senderImageView.setOnSingleClickListener {
            onImageClickListener(item.fileUrl!!)
        }
    }
}