package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.databinding.ApplyListRowItemBinding
import com.minhoi.memento.utils.ApplyStatus
import com.minhoi.memento.utils.setOnSingleClickListener

class ApplyListAdapter(private val onClickListener: (MentoringApplyDto) -> Unit) : RecyclerView.Adapter<ApplyListAdapter.ViewHolder>() {

    private val applies = mutableListOf<Pair<MentoringApplyDto, ApplyStatus>>()

    inner class ViewHolder(private val binding: ApplyListRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnSingleClickListener {
                onClickListener(applies[bindingAdapterPosition].first)
            }
        }

        fun bind(item: Pair<MentoringApplyDto, ApplyStatus>) {
            binding.applyTitle.text = item.first.boardTitle
            when (item.second) {
                ApplyStatus.ACCEPTANCE_PENDING -> {
                    binding.applyStatus.text = "승인 대기중"
                    binding.applyStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_blue_light))
                }
                ApplyStatus.ACCEPTED -> {
                    binding.applyStatus.text = "승인 완료"
                    binding.applyStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_light))
                }
                ApplyStatus.REJECTED -> {
                    binding.applyStatus.text = "승인 거절"
                    binding.applyStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_red_light))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ApplyListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = applies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(applies[position])
    }

    fun setList(contents: List<Pair<MentoringApplyDto, ApplyStatus>>) {
        applies.clear()
        applies.addAll(contents)
        notifyDataSetChanged()
    }
}