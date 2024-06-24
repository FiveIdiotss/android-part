package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverFileRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ReceiverFileViewHolder(
    private val binding: ReceiverFileRowItemBinding,
    private val onClickListener: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Receiver) {
        binding.root.setOnSingleClickListener {
            onClickListener(item.fileUrl!!)
        }
        binding.receiverData = item
    }
}