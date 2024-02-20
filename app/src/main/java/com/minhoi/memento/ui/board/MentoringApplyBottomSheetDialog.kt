package com.minhoi.memento.ui.board

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.adapter.SelectTimeTableAdapter
import com.minhoi.memento.data.dto.TimeTableDto
import com.minhoi.memento.databinding.BottomSheetFragmentMentoringApplyBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MentoringApplyBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var viewModel: BoardViewModel
    private lateinit var timeTableAdapter: SelectTimeTableAdapter
    private lateinit var binding: BottomSheetFragmentMentoringApplyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[BoardViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_fragment_mentoring_apply, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            // 다이얼로그 크기 설정 (인자값 : DialogInterface)
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeTableAdapter = SelectTimeTableAdapter()
        binding.timeTableRv.apply {
            adapter = timeTableAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
        setTimeTable()
    }

    private fun setTimeTable() {
        viewModel.post.observe(viewLifecycleOwner) {
            val timeTable = setTimeTable(it.timeTable, it.consultTime)
            timeTableAdapter.setList(timeTable)
        }
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 85 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun setTimeTable(timeTables: List<TimeTableDto>, consultTime: Int): List<String> {
        val times = mutableListOf<String>()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        timeTables.forEach {
            var start = LocalTime.parse(it.startTime, timeFormatter)
            val end = LocalTime.parse(it.endTime, timeFormatter)
            while (start.isBefore(end) || start == end) {
                times.add(start.format(timeFormatter))
                start = start.plusMinutes(consultTime.toLong())
            }
        }
        return times
    }

}