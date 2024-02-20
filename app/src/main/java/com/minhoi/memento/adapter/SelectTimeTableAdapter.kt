package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.databinding.TimetableSelectItemBinding

class SelectTimeTableAdapter : RecyclerView.Adapter<SelectTimeTableAdapter.TimeTableViewHolder>() {

    private val timeTable = mutableListOf<String>()

    inner class TimeTableViewHolder(private val binding: TimetableSelectItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.startTime.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val binding = TimetableSelectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeTableViewHolder(binding)
    }

    override fun getItemCount(): Int = timeTable.size

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(timeTable[position])
    }

    fun setList(items: List<String>) {
        timeTable.clear()
        timeTable.addAll(items)
        notifyDataSetChanged()
    }
}