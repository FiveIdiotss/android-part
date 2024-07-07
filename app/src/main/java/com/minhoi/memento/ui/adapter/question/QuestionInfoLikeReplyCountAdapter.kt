package com.minhoi.memento.ui.adapter.question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.databinding.QuestionInfoLikereplycountLayoutBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class QuestionInfoLikeReplyCountAdapter
    (private val onLikeClickListener: (Long) -> Unit) :
    RecyclerView.Adapter<QuestionInfoLikeReplyCountAdapter.ViewHolder>() {
    private var questionContent: QuestionContent? = null

    inner class ViewHolder(private val binding: QuestionInfoLikereplycountLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionContent) {
            binding.likeLayout.setOnSingleClickListener {
                onLikeClickListener(item.questionId)
            }
            binding.questionContent = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuestionInfoLikereplycountLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = if (questionContent == null) 0 else 1

    override fun onBindViewHolder(
        holder: QuestionInfoLikeReplyCountAdapter.ViewHolder,
        position: Int,
    ) {
        questionContent?.let { holder.bind(it) }
    }

    fun setContent(item: QuestionContent) {
        questionContent = item
        notifyDataSetChanged()
    }
}