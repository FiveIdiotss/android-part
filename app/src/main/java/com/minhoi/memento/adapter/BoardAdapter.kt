package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.databinding.PreviewBoardRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class BoardAdapter(private val onItemClickListener: (BoardContentDto) -> Unit) :
    PagingDataAdapter<BoardContentDto, BoardAdapter.BoardViewHolder>(BoardContentDtoDiffCallback()) {

    inner class BoardViewHolder(private val binding: PreviewBoardRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardContentDto) {
            binding.boardContent = item
            binding.root.setOnSingleClickListener {
                onItemClickListener(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PreviewBoardRowItemBinding.inflate(inflater, parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    private class BoardContentDtoDiffCallback : DiffUtil.ItemCallback<BoardContentDto>() {
        override fun areItemsTheSame(oldItem: BoardContentDto, newItem: BoardContentDto): Boolean {
            return oldItem.boardId == newItem.boardId
        }

        override fun areContentsTheSame(
            oldItem: BoardContentDto,
            newItem: BoardContentDto
        ): Boolean {
            return oldItem == newItem
        }
    }
}