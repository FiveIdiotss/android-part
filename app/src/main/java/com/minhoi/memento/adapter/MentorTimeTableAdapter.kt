package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.TimeTableDto
import com.minhoi.memento.databinding.TimetableRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class MentorTimeTableAdapter(private val onDeleteClickListener: (Int) -> Unit) :
    ListAdapter<TimeTableDto, RecyclerView.ViewHolder>(DiffCallback()) {
    inner class TimeTableViewHolder(private val binding: TimetableRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.deleteTimeTableButton.setOnSingleClickListener {
                onDeleteClickListener(bindingAdapterPosition)
            }
        }

        fun bind(item: TimeTableDto) {
            binding.timetable = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TimetableRowItemBinding.inflate(inflater, parent, false)
        return TimeTableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TimeTableViewHolder).bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<TimeTableDto>() {
        override fun areItemsTheSame(oldItem: TimeTableDto, newItem: TimeTableDto): Boolean {
            return oldItem.startTime == newItem.startTime
        }

        override fun areContentsTheSame(oldItem: TimeTableDto, newItem: TimeTableDto): Boolean {
            return oldItem.startTime == newItem.startTime
        }
    }
}


