package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.TimeTableDto
import com.minhoi.memento.databinding.TimetableRowItemBinding

class MentorTimeTableAdapter(private val onDeleteClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<MentorTimeTableAdapter.TimeTableViewHolder>() {
    private val timeTables = mutableListOf<TimeTableDto>()

    inner class TimeTableViewHolder(private val binding: TimetableRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeTableDto) {
            binding.timetable = item
            onDeleteClickListener(bindingAdapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val binding =
            TimetableRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeTableViewHolder(binding)
    }

    override fun getItemCount(): Int = timeTables.size

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(timeTables[position])
    }

    fun addTimeTable(timeTable: TimeTableDto) {
        timeTables.add(timeTable)
        notifyItemInserted(timeTables.size - 1)
    }

    fun deleteTimeTable(position: Int) {
        timeTables.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getTimeTables(): List<TimeTableDto> = timeTables


}