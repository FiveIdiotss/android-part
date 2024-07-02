package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.R
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.data.model.ChatMessageType
import com.minhoi.memento.databinding.SenderFileRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class SenderFileViewHolder(
    private val binding: SenderFileRowItemBinding,
    private val onClickListener: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Sender) {
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
        binding.root.setOnSingleClickListener {
            onClickListener(item.fileUrl!!)
        }
        binding.senderData = item
    }
}