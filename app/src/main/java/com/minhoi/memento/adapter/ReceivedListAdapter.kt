package com.minhoi.memento.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.databinding.ReceivedListRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ReceivedListAdapter(private val onClickListener: (MentoringReceivedDto) -> Unit) : RecyclerView.Adapter<ReceivedListAdapter.ViewHolder>() {

    private val receivedList = mutableListOf<MentoringReceivedDto>()

    inner class ViewHolder(private val binding: ReceivedListRowItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnSingleClickListener {
                onClickListener(receivedList[bindingAdapterPosition])
            }
        }

        fun bind(item: MentoringReceivedDto) {
            binding.recievedDto = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReceivedListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = receivedList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(receivedList[position])
    }

    fun setList(items: List<MentoringReceivedDto>) {
        receivedList.clear()
        receivedList.addAll(items)
        notifyDataSetChanged()
    }
}