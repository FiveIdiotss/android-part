package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.R
import com.minhoi.memento.databinding.TimetableSelectItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class SelectTimeTableAdapter(private val onSelectListener: (String) -> Unit) : RecyclerView.Adapter<SelectTimeTableAdapter.TimeTableViewHolder>() {

    private val timeTable = mutableListOf<String>()
    private var selectedPosition = -1

    inner class TimeTableViewHolder(private val binding: TimetableSelectItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnSingleClickListener {
                when (selectedPosition) {
                    -1 -> setSingleSelection(bindingAdapterPosition)
                    else -> {
                        notifyItemChanged(selectedPosition)
                        setSingleSelection(bindingAdapterPosition)
                    }
                }
                notifyItemChanged(selectedPosition)
            }
        }

        fun bind(item: String) {
            binding.startTime.text = item.substring(0,5)
            onSelectListener(timeTable[bindingAdapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val binding = TimetableSelectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeTableViewHolder(binding)
    }

    override fun getItemCount(): Int = timeTable.size

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        holder.bind(timeTable[position])
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.timetable_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.timetable_unselected)
        }
    }

    // Adapter 생성 후 최초 1회만 실행되는 함수
    fun setList(items: List<String>) {
        timeTable.clear()
        timeTable.addAll(items)
        notifyDataSetChanged()
    }

    fun setSingleSelection(position: Int) {
        selectedPosition = position
    }
}