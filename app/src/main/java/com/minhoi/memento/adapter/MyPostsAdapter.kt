package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.databinding.BoardRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class MyPostsAdapter(
    private val onItemClickListener: (Long) -> Unit,
    private val onBookmarkClickListener: (BoardContentDto) -> Unit
) : ListAdapter<BoardContentDto, RecyclerView.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: BoardRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnSingleClickListener {
                onItemClickListener(getItem(bindingAdapterPosition).boardId)
            }
            binding.bookmarkBtn.setOnSingleClickListener {
                onBookmarkClickListener(getItem(bindingAdapterPosition))
            }
        }

        fun bind(item: BoardContentDto) {
            binding.board = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = BoardRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<BoardContentDto>() {
        override fun areItemsTheSame(oldItem: BoardContentDto, newItem: BoardContentDto): Boolean {
            return oldItem.boardId == newItem.boardId
        }

        override fun areContentsTheSame(oldItem: BoardContentDto, newItem: BoardContentDto): Boolean {
            return oldItem == newItem
        }
    }
}