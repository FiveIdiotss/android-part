package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverMentorExtendRequestRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ReceiverMentoringExtendRequestViewHolder(
    private val binding: ReceiverMentorExtendRequestRowItemBinding,
    private val onAcceptClickListener: (Long) -> Unit,
    private val onRejectClickListener: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Receiver) {
        binding.apply {
            extendAcceptBtn.setOnSingleClickListener {
                onAcceptClickListener(item.chatId)
            }
            extendRejectBtn.setOnSingleClickListener {
                onRejectClickListener(item.chatId)
            }
            receiverData = item
        }
    }
}