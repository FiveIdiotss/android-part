package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.board.BoardContentDto
import com.minhoi.memento.databinding.BoardRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class BoardAdapter(
    private val onItemClickListener: (BoardContentDto) -> Unit,
    private val onBookmarkClickListener: (BoardContentDto) -> Unit
) : PagingDataAdapter<BoardContentDto, BoardAdapter.BoardViewHolder>(BoardContentDtoDiffCallback()) {

    inner class BoardViewHolder(private val binding: BoardRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardContentDto) {
            binding.bookmarkBtn.setOnSingleClickListener {
                onBookmarkClickListener(item)
            }
            binding.root.setOnSingleClickListener {
                onItemClickListener(item)
            }
            binding.board = item
            when (item.isBookmarked) {
                true -> binding.bookmarkBtn.setImageResource(com.minhoi.memento.R.drawable.heart_filled)
                false -> binding.bookmarkBtn.setImageResource(com.minhoi.memento.R.drawable.heart_empty)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BoardRowItemBinding.inflate(inflater, parent, false)
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

    fun modify(id: Long) {
        val currentList = snapshot().toMutableList()
        val index = currentList.indexOfFirst { it?.boardId == id }
        if (index != -1) {
            currentList[index]?.isBookmarked = !currentList[index]?.isBookmarked!!
        }
    }
}