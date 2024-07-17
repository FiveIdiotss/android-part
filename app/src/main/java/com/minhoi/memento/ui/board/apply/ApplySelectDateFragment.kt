package com.minhoi.memento.ui.board.apply

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.data.dto.board.TimeTableDto
import com.minhoi.memento.databinding.FragmentApplySelectDateBinding
import com.minhoi.memento.ui.adapter.SelectTimeTableAdapter
import com.minhoi.memento.ui.board.BoardViewModel
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ApplySelectDateFragment : BaseFragment<FragmentApplySelectDateBinding>() {
    override val layoutResourceId: Int = R.layout.fragment_apply_select_date
    private lateinit var viewModel: BoardViewModel
    private lateinit var timeTableAdapter: SelectTimeTableAdapter

    override fun initView() {
        viewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]
        binding.viewModel = viewModel
        timeTableAdapter = SelectTimeTableAdapter {
            // onSelectListener
            viewModel.setSelectedTime(it)
        }

        binding.timeTableRv.apply {
            adapter = timeTableAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.inputMessage.addTextChangedListener(messageLengthTextWatcher)

        binding.nextBtn.setOnSingleClickListener {
            with(viewModel) {
                when {
                    selectedDate.isEmpty() -> requireContext().showToast("상담 날짜는 필수 선택사항입니다.")
                    selectedTime.isEmpty() -> requireContext().showToast("상담 시간은 필수 선택사항입니다.")
                    else -> {
                        setMentoringMessage(binding.inputMessage.text.toString())
                        findNavController().navigate(R.id.action_applySelectDateFragment_to_applyShowResultFragment)
                    }
                }
            }
        }

        observeTimeTable()
    }

    private fun observeTimeTable() {
        viewModel.post.observe(this) {
            Log.d("ApplyMentoringActivity", "observeTimeTable: $it")
            val timeTable = setTimeTable(it.timeTable, it.consultTime)
            timeTableAdapter.setList(timeTable)
        }
    }

    fun setTimeTable(timeTables: List<TimeTableDto>, consultTime: Int): List<String> {
        val times = mutableListOf<String>()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        timeTables.forEach {
            var start = LocalTime.parse(it.startTime, timeFormatter)
            val end = LocalTime.parse(it.endTime, timeFormatter)
            while (start.isBefore(end)) {
                times.add(start.format(timeFormatter))
                start = start.plusMinutes(consultTime.toLong())
            }
        }
        return times.sorted()
    }

    private val messageLengthTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.inputQuestionContentLength.text = "${s?.length ?: 0} / ${200}"
            when (s?.length) {
                200 -> {
                    binding.inputQuestionContentLength.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    binding.inputMessage.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_corner_red_color
                    )
                }

                else -> {
                    binding.inputQuestionContentLength.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    binding.inputMessage.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.round_corner_black_color
                    )
                }
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }
}