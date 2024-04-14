package com.minhoi.memento.ui.board

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.SelectTimeTableAdapter
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.data.dto.TimeTableDto
import com.minhoi.memento.databinding.FragmentApplySelectDateBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
}