package com.minhoi.memento.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverMentorExtendRequestRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ReceiverMentoringExtendRequestViewHolder(
    private val binding: ReceiverMentorExtendRequestRowItemBinding,
    private val onAcceptClickListener: () -> Unit,
    private val onRejectClickListener: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.apply {
            extendAcceptBtn.setOnSingleClickListener {
                onAcceptClickListener()
            }
            extendRejectBtn.setOnSingleClickListener {
                onRejectClickListener()
            }
        }
    }
    fun bind(item: Receiver) {
        binding.receiverData = item
    }
}