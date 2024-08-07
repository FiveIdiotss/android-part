package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.board.BoardContentDto
import com.minhoi.memento.databinding.BoardRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class BoardPreviewAdapter(private val onClickListener: (BoardContentDto) -> Unit) : RecyclerView.Adapter<BoardPreviewAdapter.ViewHolder>() {

    private val boardContents = mutableListOf<BoardContentDto>()
    inner class ViewHolder(private val binding: BoardRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardContentDto) {
            binding.board = item
            binding.root.setOnSingleClickListener { onClickListener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BoardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = boardContents.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(boardContents[position])
    }

    fun setList(contents: List<BoardContentDto>) {
        boardContents.clear()
        boardContents.addAll(contents)
        notifyDataSetChanged()
    }
}