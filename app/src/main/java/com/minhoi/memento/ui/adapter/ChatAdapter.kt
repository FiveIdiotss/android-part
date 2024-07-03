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
import com.minhoi.memento.data.model.ChatMessageType
import com.minhoi.memento.databinding.ChatDateItemBinding
import com.minhoi.memento.databinding.ReceiverFileRowItemBinding
import com.minhoi.memento.databinding.ReceiverImageRowItemBinding
import com.minhoi.memento.databinding.ReceiverMentorExtendAcceptRowItemBinding
import com.minhoi.memento.databinding.ReceiverMentorExtendRejectRowItemBinding
import com.minhoi.memento.databinding.ReceiverMentorExtendRequestRowItemBinding
import com.minhoi.memento.databinding.ReceiverMessageRowItemBinding
import com.minhoi.memento.databinding.SenderExtendRequestRejectRowItemBinding
import com.minhoi.memento.databinding.SenderExtendRequestRowItemBinding
import com.minhoi.memento.databinding.SenderFileRowItemBinding
import com.minhoi.memento.databinding.SenderImageRowItemBinding
import com.minhoi.memento.databinding.SenderMentorExtendAcceptRowItemBinding
import com.minhoi.memento.databinding.SenderMessageRowItemBinding

class ChatAdapter(
    private val onImageClickListener: (String) -> Unit,
    private val onFileClickListener: (String) -> Unit,
    private val onExtendAcceptClickListener: (Long) -> Unit,
    private val onExtendRejectClickListener: (Long) -> Unit
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(
    DiffCallback()
) {

    inner class SenderViewHolder(private val binding: SenderMessageRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Sender) {
            binding.senderData = item
        }
    }

    inner class ReceiverViewHolder(private val binding: ReceiverMessageRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Receiver) {
            binding.receiverData = item
        }
    }

    inner class DateViewHolder(private val binding: ChatDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
                SenderFileViewHolder(binding) { fileUrl ->
                    onFileClickListener(fileUrl)
                }
            }

            VIEW_TYPE_SENDER_EXTEND_REQUEST -> {
                val binding = SenderExtendRequestRowItemBinding.inflate(inflater, parent, false)
                SenderMentoringExtendRequestViewHolder(binding)
            }

            VIEW_TYPE_SENDER_EXTEND_ACCEPT -> {
                val binding =
                    SenderMentorExtendAcceptRowItemBinding.inflate(inflater, parent, false)
                SenderMentoringExtendAcceptViewHolder(binding)
            }

            VIEW_TYPE_SENDER_EXTEND_REJECT -> {
                val binding =
                    SenderExtendRequestRejectRowItemBinding.inflate(inflater, parent, false)
                SenderMentoringExtendRejectViewHolder(binding)
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
                ReceiverFileViewHolder(binding) { fileUrl ->
                    onFileClickListener(fileUrl)
                }
            }

            VIEW_TYPE_RECEIVER_EXTEND_REQUEST -> {
                val binding =
                    ReceiverMentorExtendRequestRowItemBinding.inflate(inflater, parent, false)
                ReceiverMentoringExtendRequestViewHolder(
                    binding = binding,
                    onAcceptClickListener = { onExtendAcceptClickListener(it) },
                    onRejectClickListener = { onExtendRejectClickListener(it) }
                )
            }

            VIEW_TYPE_RECEIVER_EXTEND_ACCEPT -> {
                val binding =
                    ReceiverMentorExtendAcceptRowItemBinding.inflate(inflater, parent, false)
                ReceiverMentoringExtendAcceptViewHolder(binding)
            }

            VIEW_TYPE_RECEIVER_EXTEND_REJECT -> {
                val binding =
                    ReceiverMentorExtendRejectRowItemBinding.inflate(inflater, parent, false)
                ReceiverMentoringExtendRejectViewHolder(binding)
            }

            VIEW_TYPE_RECEIVER_EXTEND_COMPLETE -> {
                val binding =
                    ReceiverMentorExtendRequestRowItemBinding.inflate(inflater, parent, false)
                ReceiverMentoringExtendRequestViewHolder(
                    binding = binding,
                    isCompleteRequest = true,
                    onAcceptClickListener = {},
                    onRejectClickListener = {}
                )
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
            is SenderMentoringExtendRequestViewHolder -> holder.bind(item as Sender)
            is SenderMentoringExtendAcceptViewHolder -> holder.bind(item as Sender)
            is SenderMentoringExtendRejectViewHolder -> holder.bind(item as Sender)
            is ReceiverViewHolder -> holder.bind(item as Receiver)
            is ReceiverImageViewHolder -> holder.bind(item as Receiver)
            is ReceiverFileViewHolder -> holder.bind(item as Receiver)
            is ReceiverMentoringExtendRequestViewHolder -> holder.bind(item as Receiver)
            is ReceiverMentoringExtendAcceptViewHolder -> holder.bind(item as Receiver)
            is ReceiverMentoringExtendRejectViewHolder -> holder.bind(item as Receiver)
            is DateViewHolder -> holder.bind(item as ChatDate)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is Sender -> when (item.type) {
                ChatMessageType.TEXT -> VIEW_TYPE_SENDER_TEXT
                ChatMessageType.IMAGE -> VIEW_TYPE_SENDER_IMAGE
                ChatMessageType.CONSULT_EXTEND, ChatMessageType.CONSULT_EXTEND_COMPLETE -> VIEW_TYPE_SENDER_EXTEND_REQUEST
                ChatMessageType.CONSULT_EXTEND_ACCEPT -> VIEW_TYPE_SENDER_EXTEND_ACCEPT
                ChatMessageType.CONSULT_EXTEND_DECLINE -> VIEW_TYPE_SENDER_EXTEND_REJECT
                else -> VIEW_TYPE_SENDER_FILE
            }

            is Receiver -> when (item.type) {
                ChatMessageType.TEXT -> VIEW_TYPE_RECEIVER_TEXT
                ChatMessageType.IMAGE -> VIEW_TYPE_RECEIVER_IMAGE
                ChatMessageType.CONSULT_EXTEND -> VIEW_TYPE_RECEIVER_EXTEND_REQUEST
                ChatMessageType.CONSULT_EXTEND_ACCEPT -> VIEW_TYPE_RECEIVER_EXTEND_ACCEPT
                ChatMessageType.CONSULT_EXTEND_DECLINE -> VIEW_TYPE_RECEIVER_EXTEND_REJECT
                ChatMessageType.CONSULT_EXTEND_COMPLETE -> VIEW_TYPE_RECEIVER_EXTEND_COMPLETE
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
        private const val VIEW_TYPE_SENDER_EXTEND_REQUEST = 3
        private const val VIEW_TYPE_SENDER_EXTEND_ACCEPT = 4
        private const val VIEW_TYPE_SENDER_EXTEND_REJECT = 5
        private const val VIEW_TYPE_RECEIVER_TEXT = 6
        private const val VIEW_TYPE_RECEIVER_IMAGE = 7
        private const val VIEW_TYPE_RECEIVER_FILE = 8
        private const val VIEW_TYPE_RECEIVER_EXTEND_REQUEST = 9
        private const val VIEW_TYPE_RECEIVER_EXTEND_ACCEPT = 10
        private const val VIEW_TYPE_RECEIVER_EXTEND_REJECT = 11
        private const val VIEW_TYPE_RECEIVER_EXTEND_COMPLETE = 12
        private const val VIEW_TYPE_DATE = 13

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