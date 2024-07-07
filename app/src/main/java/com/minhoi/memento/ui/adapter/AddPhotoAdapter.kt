package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.minhoi.memento.databinding.AddPhotoRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class AddPhotoAdapter(private val onDeleteClickListener: (Int) -> Unit) :
    ListAdapter<String, AddPhotoAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: AddPhotoRowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.deleteBtn.setOnSingleClickListener {
                onDeleteClickListener(bindingAdapterPosition)
            }
        }

        fun bind(url: String) {
            Glide.with(binding.image.context)
                .load(url)
                .apply(RequestOptions().centerCrop())
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AddPhotoRowItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = getItem(position)
        holder.bind(url)
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}