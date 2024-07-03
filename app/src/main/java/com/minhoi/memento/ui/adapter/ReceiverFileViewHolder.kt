package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.R
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.data.model.ChatMessageType
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
        when (item.type) {
            ChatMessageType.PDF -> {
                binding.fileTypeImageView.setImageResource(R.drawable.pdffile)
            }
            ChatMessageType.ZIP -> {
                binding.fileTypeImageView.setImageResource(R.drawable.zipfile)
            }
            else -> {
                binding.fileTypeImageView.setImageResource(R.drawable.etcfile)
            }
        }
        binding.receiverData = item
    }
}