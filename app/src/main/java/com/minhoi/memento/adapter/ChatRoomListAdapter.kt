package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.databinding.ChatroomListRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ChatRoomListAdapter(private val onClickListener: (ChatRoom) -> Unit) : RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder>() {
    private val chats = mutableListOf<ChatRoom>()

    inner class ViewHolder(private val binding: ChatroomListRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnSingleClickListener {
                onClickListener(chats[bindingAdapterPosition])
            }
        }

        fun bind(item: ChatRoom) {
            binding.chatRoom = item
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChatroomListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    fun setList(chats: List<ChatRoom>) {
        this.chats.clear()
        this.chats.addAll(chats)
        notifyDataSetChanged()
    }
}