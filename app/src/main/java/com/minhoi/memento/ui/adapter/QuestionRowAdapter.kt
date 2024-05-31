package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.databinding.QuestionBoardRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class QuestionRowAdapter(
    private val onClickListener: (Long) -> Unit,
) : PagingDataAdapter<QuestionContent, QuestionRowAdapter.QuestionRowViewHolder>(
    QuestionDiffCallback()
) {

    inner class QuestionRowViewHolder(private val binding: QuestionBoardRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionContent) {
            binding.root.setOnSingleClickListener {
                onClickListener(item.questionId)
            }
            binding.questionData = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionRowViewHolder {
        val binding = QuestionBoardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionRowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionRowViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }
}



