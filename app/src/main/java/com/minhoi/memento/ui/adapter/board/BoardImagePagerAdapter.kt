package com.minhoi.memento.ui.adapter.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.BoardImage
import com.minhoi.memento.databinding.BoardImagePagerItemBinding

class BoardImagePagerAdapter : ListAdapter<BoardImage, BoardImagePagerAdapter.ViewHolder>(ImageDiffCallback()) {

    inner class ViewHolder(private val binding: BoardImagePagerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BoardImage) {
            binding.imageUrl = item.boardImageUrl
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BoardImagePagerItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<BoardImage>() {
        override fun areItemsTheSame(oldItem: BoardImage, newItem: BoardImage): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BoardImage, newItem: BoardImage): Boolean {
            return oldItem == newItem
        }
    }
}