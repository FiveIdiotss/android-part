package com.minhoi.memento.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.databinding.IntroLayoutPagerItemBinding

class IntroPagerAdapter(private var pageList: List<String>) :
    RecyclerView.Adapter<IntroPagerAdapter.IntroPagerViewHolder>() {

    inner class IntroPagerViewHolder(private val binding: IntroLayoutPagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.introTextView.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroPagerViewHolder {
        val binding =
            IntroLayoutPagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntroPagerViewHolder(binding)
    }

    override fun getItemCount(): Int = pageList.size

    override fun onBindViewHolder(holder: IntroPagerViewHolder, position: Int) {
        holder.bind(pageList[position])
    }
}