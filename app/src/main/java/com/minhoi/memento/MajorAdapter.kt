package com.minhoi.memento

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.databinding.MajorRowItemBinding

class MajorAdapter : RecyclerView.Adapter<MajorAdapter.ViewHolder>() {

    private val majors = mutableListOf<String>()

    inner class ViewHolder(private val binding: MajorRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.majorName.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = DataBindingUtil.inflate<MajorRowItemBinding>(LayoutInflater.from(parent.context), R.layout.major_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return majors.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(majors[position])
    }

    // 초기 1회만 실행되기 때문에 notifyDataSetChanged 성능 이슈 X
    fun setItems(items: List<String>) {
        majors.addAll(items)
        notifyDataSetChanged()
    }
}