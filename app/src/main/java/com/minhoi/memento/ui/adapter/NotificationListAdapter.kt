package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.notification.NotificationListDto
import com.minhoi.memento.data.model.NotificationListType
import com.minhoi.memento.databinding.NotificationRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class NotificationListAdapter(private val onClickListener: (NotificationListDto) -> Unit) :
    ListAdapter<NotificationListDto, NotificationListAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: NotificationRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationListDto) {
            binding.notification = item
            when (item.type) {
                NotificationListType.REPLY_QUEST -> {
                    binding.apply {
                        root.setOnSingleClickListener {
                            onClickListener(item)
                        }
                        notificationTitle.text = String.format(item.type.message, item.title)
                        notificationContent.text = item.content
                    }
                }
                NotificationListType.APPLY -> {
                    binding.apply {
                        root.setOnSingleClickListener {
                            onClickListener(item)
                        }
                        notificationTitle.text = String.format(item.type.message, item.senderName, item.title)
                        notificationContent.visibility = View.GONE
                    }
                }
                NotificationListType.MATCHING_COMPLETE -> {}
                NotificationListType.MATCHING_DECLINE -> {}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotificationRowItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback() : DiffUtil.ItemCallback<NotificationListDto>() {
        override fun areItemsTheSame(
            oldItem: NotificationListDto,
            newItem: NotificationListDto,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NotificationListDto,
            newItem: NotificationListDto,
        ): Boolean {
            return oldItem == newItem
        }
    }
}