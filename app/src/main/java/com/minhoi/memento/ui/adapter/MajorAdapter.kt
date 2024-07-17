package com.minhoi.memento.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.R
import com.minhoi.memento.data.dto.join.MajorDto
import com.minhoi.memento.databinding.MajorRowItemBinding

class MajorAdapter(private val context: Context, private val onClickListener: (MajorDto) -> Unit) : RecyclerView.Adapter<MajorAdapter.ViewHolder>() {

    private val majors = mutableListOf<MajorDto>()
    private var selectedPosition = -1

    inner class ViewHolder(private val binding: MajorRowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(selectedPosition)  // 이전에 선택된 아이템의 배경색을 원래대로 되돌림
                    selectedPosition = position
                    notifyItemChanged(position)
                    onClickListener(majors[position])
                }
            }
        }

        fun bind(item: MajorDto) {
            binding.majorName.text = item.name

            if (position == selectedPosition) {
                binding.majorName.setTextColor(ContextCompat.getColor(context, R.color.app_theme))
                binding.majorName.setTypeface(null, Typeface.BOLD)
            } else {
                binding.majorName.setTextColor(ContextCompat.getColor(context, R.color.black))
                binding.majorName.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = DataBindingUtil.inflate<MajorRowItemBinding>(LayoutInflater.from(parent.context),
            R.layout.major_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return majors.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(majors[position])
    }

    // 초기 1회만 실행되기 때문에 notifyDataSetChanged 성능 이슈 X
    fun setItems(items: List<MajorDto>) {
        majors.clear()
        majors.addAll(items)
        notifyDataSetChanged()
    }
}