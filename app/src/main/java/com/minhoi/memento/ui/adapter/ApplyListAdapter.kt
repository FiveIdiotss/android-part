package com.minhoi.memento.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.MentoringApplyListDto
import com.minhoi.memento.databinding.ApplyListRowItemBinding
import com.minhoi.memento.data.model.ApplyStatus
import com.minhoi.memento.utils.setOnSingleClickListener

class ApplyListAdapter(
    private val onBoardClickListener: (Long) -> Unit,
    private val onShowApplyContentListener: (MentoringApplyListDto) -> Unit
) : RecyclerView.Adapter<com.minhoi.memento.ui.adapter.ApplyListAdapter.ViewHolder>() {

    private val applies = mutableListOf<Pair<MentoringApplyListDto, ApplyStatus>>()

    inner class ViewHolder(private val binding: ApplyListRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.showApplyContent.setOnSingleClickListener {
                onShowApplyContentListener(applies[bindingAdapterPosition].first)
            }

            binding.root.setOnSingleClickListener {
                onBoardClickListener(applies[bindingAdapterPosition].first.boardId)
            }
        }

        fun bind(item: Pair<MentoringApplyListDto, ApplyStatus>) {
            Log.d("ApplyListAdapter", "bind: ${item.first}")
//            binding.board = BoardContentDto(
//                    item.first.boardId,
//                    item.first.otherMemberName,
//                    item.first.boardTitle,
//                    "가천대학교",
//                    "컴퓨터공학과",
//                0,
//                "",
//                "",
//                "",
//                item.first.otherMemberId
//            )

            when (item.second) {
                ApplyStatus.ACCEPTANCE_PENDING -> {
                    binding.applyStatus.text = "신청 대기중"
                    binding.applyCancelLayout.visibility = View.VISIBLE
                    binding.applyStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_blue_light))
                }
                ApplyStatus.ACCEPTED -> {
                    binding.applyStatus.text = "멘토링 매칭중"
                    binding.applyCancelLayout.visibility = View.GONE
                    binding.applyStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_light))
                }
                ApplyStatus.REJECTED -> {
                    binding.applyStatus.text = "승인 거절"
                    binding.applyStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_red_light))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): _root_ide_package_.com.minhoi.memento.ui.adapter.ApplyListAdapter.ViewHolder {
        val binding = ApplyListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = applies.size

    override fun onBindViewHolder(holder: _root_ide_package_.com.minhoi.memento.ui.adapter.ApplyListAdapter.ViewHolder, position: Int) {
        holder.bind(applies[position])
    }

    fun setList(contents: List<Pair<MentoringApplyListDto, ApplyStatus>>) {
        applies.clear()
        applies.addAll(contents)
        notifyDataSetChanged()
    }
}