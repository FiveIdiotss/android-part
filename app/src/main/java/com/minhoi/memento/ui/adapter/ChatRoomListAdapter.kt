package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.member.MemberDTO
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.databinding.ChatroomListRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ChatRoomListAdapter(private val onClickListener: (ChatRoom) -> Unit) : RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder>() {
    private val chats = mutableListOf<Pair<ChatRoom, MemberDTO>>()

    inner class ViewHolder(private val binding: ChatroomListRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnSingleClickListener {
                onClickListener(chats[bindingAdapterPosition].first)
            }
        }

        fun bind(item: Pair<ChatRoom, MemberDTO>) {
            binding.apply {
                chatRoom = item.first
                member = item.second
                executePendingBindings()
            }
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

    fun setList(chats: List<Pair<ChatRoom, MemberDTO>>) {
        this.chats.clear()
        this.chats.addAll(chats)
        notifyDataSetChanged()
    }
}