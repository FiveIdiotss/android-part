package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.databinding.ApplyListRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ApplyListAdapter(private val onClickListener: (MentoringApplyDto) -> Unit) : RecyclerView.Adapter<ApplyListAdapter.ViewHolder>() {

    private val applies = mutableListOf<MentoringApplyDto>()

    inner class ViewHolder(private val binding: ApplyListRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnSingleClickListener {
                onClickListener(applies[bindingAdapterPosition])
            }
        }

        fun bind(item: MentoringApplyDto) {
            binding.applyTitle.text = item.boardTitle
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

    fun setList(contents: List<MentoringApplyDto>) {
        applies.clear()
        applies.addAll(contents)
        notifyDataSetChanged()
    }
}