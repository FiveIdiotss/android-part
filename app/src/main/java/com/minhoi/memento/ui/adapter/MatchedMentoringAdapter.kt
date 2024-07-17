package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.minhoi.memento.data.dto.member.MemberDTO
import com.minhoi.memento.data.dto.mentoring.MentoringMatchInfo
import com.minhoi.memento.databinding.MatchedMentoringRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class MatchedMentoringAdapter(private val onChatClickListener: (MentoringMatchInfo) -> Unit) : RecyclerView.Adapter<MatchedMentoringAdapter.ViewHolder>()   {

    private val matchedMentoringList = mutableListOf<Pair<MentoringMatchInfo, MemberDTO>>()

    inner class ViewHolder(private val binding: MatchedMentoringRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // 채팅 버튼 클릭시 상대방의 id를 ChatActivity로 전달
        init {
            binding.chatBtn.setOnSingleClickListener {
                onChatClickListener(matchedMentoringList[bindingAdapterPosition].first)
            }
        }

        fun bind(item: Pair<MentoringMatchInfo, MemberDTO>) {
            binding.member = item.second
            Glide.with(binding.root)
                .load(item.second.profileImageUrl)
                .into(binding.imageView2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MatchedMentoringRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = matchedMentoringList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(matchedMentoringList[position])
    }

    fun setList(matchedMentoringList: List<Pair<MentoringMatchInfo, MemberDTO>>) {
        this.matchedMentoringList.clear()
        this.matchedMentoringList.addAll(matchedMentoringList)
        notifyDataSetChanged()
    }
}