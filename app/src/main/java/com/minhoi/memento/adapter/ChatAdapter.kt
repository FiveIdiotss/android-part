package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.ChatMessage
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.ReceiverMessageRowItemBinding
import com.minhoi.memento.databinding.SenderMessageRowItemBinding

class ChatAdapter(private val onImageClicked: (String) -> Unit) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    inner class SenderViewHolder(private val binding: SenderMessageRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.senderImageView.setOnClickListener {
                val position = bindingAdapterPosition
                val item = getItem(position) as Sender
                onImageClicked(item.image!!)
            }
        }
        fun bind(item: Sender) {
            binding.senderData = item
            if (item.image == "null" || item.image == null) {
                binding.senderImageView.visibility = View.GONE
                binding.senderMessage.visibility = View.VISIBLE
            } else {
                binding.senderImageView.visibility = View.VISIBLE
                binding.senderMessage.visibility = View.GONE
            }
            if (!item.showDate) {
                binding.senderDate.visibility = View.GONE
            } else {
                binding.senderDate.visibility = View.VISIBLE
            }
        }
    }

    inner class ReceiverViewHolder(private val binding: ReceiverMessageRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.receiverImageView.setOnClickListener {
                val position = bindingAdapterPosition
                val item = getItem(position) as Receiver
                onImageClicked(item.image!!)
            }
        }
        fun bind(item: Receiver) {
            binding.receiverData = item
            if (item.image == "null" || item.image == null) {
                binding.receiverImageView.visibility = View.GONE
                binding.receiverMessage.visibility = View.VISIBLE
            } else {
                binding.receiverImageView.visibility = View.VISIBLE
                binding.receiverMessage.visibility = View.GONE
            }
            if (!item.showDate) {
                binding.apply {
                    receiverDate.visibility = View.GONE
                    profileImage.visibility = View.INVISIBLE
                    receiverName.visibility = View.GONE
                }
            } else {
                binding.apply {
                    receiverDate.visibility = View.VISIBLE
                    profileImage.visibility = View.VISIBLE
                    receiverName.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                val binding = SenderMessageRowItemBinding.inflate(inflater, parent, false)
                SenderViewHolder(binding)
            }

            VIEW_TYPE_RECEIVER -> {
                val binding = ReceiverMessageRowItemBinding.inflate(inflater, parent, false)
                ReceiverViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SenderViewHolder -> holder.bind(item as Sender)
            is ReceiverViewHolder -> holder.bind(item as Receiver)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is Sender -> VIEW_TYPE_SENDER
            is Receiver -> VIEW_TYPE_RECEIVER
            else -> throw IllegalArgumentException("Invalid message type")
        }
    }

    companion object {
        private const val VIEW_TYPE_SENDER = 0
        private const val VIEW_TYPE_RECEIVER = 1
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