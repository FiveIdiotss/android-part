package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.MentoringApplyListDto
import com.minhoi.memento.data.model.ApplyStatus
import com.minhoi.memento.databinding.ApplyListRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ApplyListAdapter(
    private val onBoardClickListener: (Long) -> Unit,
    private val onShowApplyContentListener: (MentoringApplyListDto) -> Unit
) : ListAdapter<Pair<MentoringApplyListDto, BoardContentDto>, ApplyListAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ApplyListRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.showApplyContent.setOnSingleClickListener {
                onShowApplyContentListener(currentList[bindingAdapterPosition].first)
            }

            binding.root.setOnSingleClickListener {
                onBoardClickListener(currentList[bindingAdapterPosition].second.boardId)
            }
        }

        fun bind(item: Pair<MentoringApplyListDto, BoardContentDto>) {
            when (item.first.applyState) {
                ApplyStatus.HOLDING -> {
                    binding.applyStatus.text = "신청 대기중"
                    binding.applyCancelLayout.visibility = View.VISIBLE
                }
                ApplyStatus.COMPLETE -> {
                    binding.applyStatus.text = "멘토링 신청이 수락되었습니다. 채팅 목록을 확인해주세요!"
                    binding.applyCancelLayout.visibility = View.GONE
                }
                ApplyStatus.REJECT -> {
                    binding.applyStatus.text = "신청이 거절되었습니다. 아래의 거절 사유를 확인해주세요."
                    binding.applyCancelLayout.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplyListAdapter.ViewHolder {
        val binding = ApplyListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ApplyListAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback() : DiffUtil.ItemCallback<Pair<MentoringApplyListDto, BoardContentDto>>() {
        override fun areItemsTheSame(oldItem: Pair<MentoringApplyListDto, BoardContentDto>, newItem: Pair<MentoringApplyListDto, BoardContentDto>): Boolean {
            return oldItem.first.applyId == newItem.first.applyId
        }

        override fun areContentsTheSame(oldItem: Pair<MentoringApplyListDto, BoardContentDto>, newItem: Pair<MentoringApplyListDto, BoardContentDto>): Boolean {
            return oldItem == newItem
        }
    }
}