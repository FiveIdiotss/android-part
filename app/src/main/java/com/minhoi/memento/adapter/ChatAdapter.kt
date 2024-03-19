package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.ChatMessage
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.databinding.ReceiverMessageRowItemBinding
import com.minhoi.memento.databinding.SenderMessageRowItemBinding

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

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

    fun addMessages(messages: List<ChatMessage>) {
        val currentList = currentList.toMutableList()
        currentList.addAll(0, messages)
        submitList(currentList)
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