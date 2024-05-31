package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverImageRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ReceiverImageViewHolder(
    private val binding: ReceiverImageRowItemBinding,
    private val onImageClickListener: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Receiver) {
        binding.receiverData = item
        binding.receiverImageView.setOnSingleClickListener {
            onImageClickListener(item.fileUrl!!)
        }
    }
}