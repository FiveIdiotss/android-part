package com.minhoi.memento.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.databinding.ReceiverMentorExtendRequestRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ReceiverMentoringExtendRequestViewHolder(
    private val binding: ReceiverMentorExtendRequestRowItemBinding,
    private val isCompleteRequest: Boolean = false,
    private val onAcceptClickListener: (Long) -> Unit,
    private val onRejectClickListener: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Receiver) {
        if (isCompleteRequest) {
            binding.apply {
                extendAcceptBtn.visibility = View.GONE
                extendRejectBtn.visibility = View.GONE
                extendTime.visibility = View.GONE
                requestStatus.visibility = View.VISIBLE
            }
        }
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