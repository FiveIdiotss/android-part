package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.R
import com.minhoi.memento.databinding.ListItemFooterBinding

class BoardLoadStateAdapter : LoadStateAdapter<BoardLoadStateAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ListItemFooterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.loadingProgressBar.isVisible = loadState is LoadState.Loading
            binding.errorMessageTextView.isVisible = loadState is LoadState.Error
            binding.retryButton.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) = holder.bind(loadState)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_footer, parent, false)
        val binding = ListItemFooterBinding.bind(view)
        return ViewHolder(binding)
    }
}