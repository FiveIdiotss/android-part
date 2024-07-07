package com.minhoi.memento.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.R
import com.minhoi.memento.data.dto.BoardContentDto
import com.minhoi.memento.data.dto.BoardContentForReceived
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.data.model.ApplyStatus
import com.minhoi.memento.databinding.ReceivedListInBoardRowItemBinding
import com.minhoi.memento.databinding.ReceivedListRowItemBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class ReceivedListAdapter(
    private val onBoardClickListener: (Long) -> Unit,
    private val onReceivedItemClickListener: (MentoringReceivedDto) -> Unit,
) : RecyclerView.Adapter<ReceivedListAdapter.ViewHolder>() {

    private val boardsWithReceivedMentoring =
        mutableListOf<Pair<BoardContentForReceived, List<MentoringReceivedDto>>>()

    inner class ViewHolder(private val binding: ReceivedListRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.receivedListLayout.setOnSingleClickListener {
                boardsWithReceivedMentoring[bindingAdapterPosition].first.isExpanded =
                    !boardsWithReceivedMentoring[bindingAdapterPosition].first.isExpanded
                notifyItemChanged(bindingAdapterPosition)
            }

            binding.root.setOnSingleClickListener {
                onBoardClickListener(
                    boardsWithReceivedMentoring[bindingAdapterPosition].first.boardId
                )
            }
        }

        fun bind(item: Pair<BoardContentForReceived, List<MentoringReceivedDto>>) {
            Log.d("ReceivedListAdapter", "binds: $item")
            val boardContent = item.first
            val unProcessMentoringCount = item.second.count {
                it.applyState == ApplyStatus.HOLDING
            }
            if (unProcessMentoringCount == 0) {
                binding.applyCount.visibility = View.GONE

            } else {
                binding.applyCount.visibility = View.VISIBLE
                binding.applyCount.text = unProcessMentoringCount.toString()
            }
            binding.board = BoardContentDto(
                boardContent.boardId,
                boardContent.memberName,
                boardContent.title,
                boardContent.school,
                boardContent.major,
                boardContent.year,
                boardContent.introduction,
                boardContent.target,
                boardContent.content,
                boardContent.memberId,
                boardContent.memberImageUrl,
                boardContent.representImageUrl,
                boardContent.isBookmarked
            )
            binding.applyListWithBoardRv.apply {
                adapter =
                    ReceivedChildAdapter(item.second, object : OnReceivedItemClickListener {
                        override fun onItemClick(item: MentoringReceivedDto) {
                            onReceivedItemClickListener(item)
                        }
                    })
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }

            if (boardContent.isExpanded) {
                binding.applyListWithBoardRv.visibility = View.VISIBLE
                binding.scrollBtn.setImageResource(R.drawable.fold)
            } else {
                binding.applyListWithBoardRv.visibility = View.GONE
                binding.scrollBtn.setImageResource(R.drawable.unfold)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ReceivedListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = boardsWithReceivedMentoring.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(boardsWithReceivedMentoring[position])
    }

    fun setList(items: List<Pair<BoardContentForReceived, List<MentoringReceivedDto>>>) {
        boardsWithReceivedMentoring.clear()
        boardsWithReceivedMentoring.addAll(items)
        notifyDataSetChanged()
    }

    inner class ReceivedChildAdapter(
        private val receivedItem: List<MentoringReceivedDto>,
        private val onReceivedItemClickListener: OnReceivedItemClickListener,
    ) :
        RecyclerView.Adapter<ReceivedChildAdapter.InnerViewHolder>() {
        inner class InnerViewHolder(private val binding: ReceivedListInBoardRowItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnSingleClickListener {
                    onReceivedItemClickListener.onItemClick(receivedItem[bindingAdapterPosition])
                }
            }

            fun bind(item: MentoringReceivedDto) {
                binding.receivedDto = item
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
            val binding = ReceivedListInBoardRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return InnerViewHolder(binding)
        }

        override fun getItemCount(): Int = receivedItem.size

        override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
            holder.bind(receivedItem[position])
        }
    }

    interface OnReceivedItemClickListener {
        fun onItemClick(item: MentoringReceivedDto)
    }
}