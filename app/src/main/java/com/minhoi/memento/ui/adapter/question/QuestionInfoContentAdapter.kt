package com.minhoi.memento.ui.adapter.question

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.question.QuestionContent
import com.minhoi.memento.databinding.QuestionInfoContentLayoutBinding

class QuestionInfoContentAdapter :
    RecyclerView.Adapter<QuestionInfoContentAdapter.ViewHolder>() {

    private var questionContent: QuestionContent? = null

    inner class ViewHolder(private val binding: QuestionInfoContentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionContent) {
            binding.questionContent = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuestionInfoContentLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = if (questionContent == null) 0 else 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        questionContent?.let { holder.bind(it) }
    }

    fun setContent(item: QuestionContent) {
        questionContent = item
        notifyDataSetChanged()
    }
}
