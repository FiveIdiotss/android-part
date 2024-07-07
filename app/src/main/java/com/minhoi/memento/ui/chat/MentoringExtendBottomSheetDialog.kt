package com.minhoi.memento.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.databinding.FragmentMentoringExtendBottomSheetDialogBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.repeatOnStarted
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showToast
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MentoringExtendBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMentoringExtendBottomSheetDialogBinding
    private val viewModel by activityViewModels<ChatViewModel>()
    private var extendClickedListener: OnExtendClickedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mentoring_extend_bottom_sheet_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFinishTime()

        binding.extendBtn.setOnSingleClickListener {
            extendClickedListener?.onExtendClicked()
        }
    }

    private fun observeFinishTime() {
        viewLifecycleOwner.repeatOnStarted {
            viewModel.chatRoomState.collect { state ->
                when (state) {
                    is UiState.Loading, UiState.Empty -> {}
                    is UiState.Success -> {
                        updateFinishTime(state.data.startTime, state.data.consultTime.toLong())
                    }
                    is UiState.Error -> requireContext().showToast(state.error!!.message!!)
                }
            }
        }
    }

    private fun updateFinishTime(startTime: String, consultTime: Long) {
        val formattedStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm:ss"))
        val beforeFinishTime = formattedStartTime.plusMinutes(consultTime)
        val afterFinishTime = beforeFinishTime.plusMinutes(consultTime)

        val formattedStartTimeString = beforeFinishTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val formattedExtendedFinishTimeString = afterFinishTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        binding.apply {
            existingFinishTime.text = formattedStartTimeString
            extendedFinishTime.text = formattedExtendedFinishTimeString
        }
    }

    interface OnExtendClickedListener {
        fun onExtendClicked()
    }

    fun setOnExtendClickedListener(listener: OnExtendClickedListener) {
        this.extendClickedListener = listener
    }
}