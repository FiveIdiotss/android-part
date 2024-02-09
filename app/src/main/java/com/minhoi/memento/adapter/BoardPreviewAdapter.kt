package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.databinding.PreviewBoardRowItemBinding

class BoardPreviewAdapter : RecyclerView.Adapter<BoardPreviewAdapter.ViewHolder>() {

    private val boardContents = mutableListOf<BoardContentDto>()
    inner class ViewHolder(private val binding: PreviewBoardRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardContentDto) {
            binding.boardContent = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PreviewBoardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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