package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.ChatDate
import com.minhoi.memento.data.dto.chat.ChatMessage
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.data.model.ChatFileType
import com.minhoi.memento.databinding.ChatDateItemBinding
import com.minhoi.memento.databinding.ReceiverFileRowItemBinding
import com.minhoi.memento.databinding.ReceiverImageRowItemBinding
import com.minhoi.memento.databinding.ReceiverMessageRowItemBinding
import com.minhoi.memento.databinding.SenderFileRowItemBinding
import com.minhoi.memento.databinding.SenderImageRowItemBinding
import com.minhoi.memento.databinding.SenderMessageRowItemBinding

class ChatAdapter(private val onImageClickListener: (String) -> Unit) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(
    DiffCallback()
) {

    inner class SenderViewHolder(private val binding: SenderMessageRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Sender) {
            binding.senderData = item
        }
    }

    inner class ReceiverViewHolder(private val binding: ReceiverMessageRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Receiver) {
            binding.receiverData = item
        }
    }

    inner class DateViewHolder(private val binding: ChatDateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatDate) {
            binding.chatDate = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENDER_TEXT -> {
                val binding = SenderMessageRowItemBinding.inflate(inflater, parent, false)
                SenderViewHolder(binding)
            }

            VIEW_TYPE_SENDER_IMAGE -> {
                val binding = SenderImageRowItemBinding.inflate(inflater, parent, false)
                SenderImageViewHolder(binding) { imageUrl ->
                    onImageClickListener(imageUrl)
                }
            }

            VIEW_TYPE_SENDER_FILE -> {
                val binding = SenderFileRowItemBinding.inflate(inflater, parent, false)
                SenderFileViewHolder(binding)
            }

            VIEW_TYPE_RECEIVER_TEXT -> {
                val binding = ReceiverMessageRowItemBinding.inflate(inflater, parent, false)
                ReceiverViewHolder(binding)
            }

            VIEW_TYPE_RECEIVER_IMAGE -> {
                val binding = ReceiverImageRowItemBinding.inflate(inflater, parent, false)
                ReceiverImageViewHolder(binding) { imageUrl ->
                    onImageClickListener(imageUrl)
                }
            }

            VIEW_TYPE_RECEIVER_FILE -> {
                val binding = ReceiverFileRowItemBinding.inflate(inflater, parent, false)
                ReceiverFileViewHolder(binding)
            }

            VIEW_TYPE_DATE -> {
                val binding = ChatDateItemBinding.inflate(inflater, parent, false)
                DateViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SenderViewHolder -> holder.bind(item as Sender)
            is SenderImageViewHolder -> holder.bind(item as Sender)
            is SenderFileViewHolder -> holder.bind(item as Sender)
            is ReceiverViewHolder -> holder.bind(item as Receiver)
            is ReceiverImageViewHolder -> holder.bind(item as Receiver)
            is ReceiverFileViewHolder -> holder.bind(item as Receiver)
            is DateViewHolder -> holder.bind(item as ChatDate)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is Sender -> when (item.type) {
                ChatFileType.MESSAGE -> VIEW_TYPE_SENDER_TEXT
                ChatFileType.IMAGE -> VIEW_TYPE_SENDER_IMAGE
                else -> VIEW_TYPE_SENDER_FILE
            }
            is Receiver -> when (item.type) {
                ChatFileType.MESSAGE -> VIEW_TYPE_RECEIVER_TEXT
                ChatFileType.IMAGE -> VIEW_TYPE_RECEIVER_IMAGE
                else -> VIEW_TYPE_RECEIVER_FILE
            }
            is ChatDate -> VIEW_TYPE_DATE
            else -> throw IllegalArgumentException("Invalid message type")
        }
    }

    companion object {
        private const val VIEW_TYPE_SENDER_TEXT = 0
        private const val VIEW_TYPE_SENDER_IMAGE = 1
        private const val VIEW_TYPE_SENDER_FILE = 2
        private const val VIEW_TYPE_RECEIVER_TEXT = 3
        private const val VIEW_TYPE_RECEIVER_IMAGE = 4
        private const val VIEW_TYPE_RECEIVER_FILE = 5
        private const val VIEW_TYPE_DATE = 6
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.date == newItem.date
        }
    }
}