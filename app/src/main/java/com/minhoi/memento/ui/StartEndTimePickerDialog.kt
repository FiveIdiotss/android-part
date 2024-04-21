package com.minhoi.memento.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.minhoi.memento.R
import com.minhoi.memento.data.dto.TimeTableDto
import com.minhoi.memento.databinding.StartEndTimepickerDialogBinding
import com.minhoi.memento.utils.containsOverlap
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.setTimeInterval

class StartEndTimePickerDialog(
    private val interval: Int,
    private val timetables: List<TimeTableDto>,
    private val onTimeSelected: (TimeTableDto) -> Unit,
) : DialogFragment() {

    private lateinit var binding: StartEndTimepickerDialogBinding
    private val times = mutableListOf<IntRange>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.start_end_timepicker_dialog,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 선택한 시간을 정수 범위로 변환
        timetables.forEach {
            times.add(IntRange(stringTimeToInt(it.startTime), stringTimeToInt(it.endTime)))
        }

        binding.apply {
            startTimePicker.setIs24HourView(true)
            endTimePicker.setIs24HourView(true)
            startTimePicker.setTimeInterval(interval)
            endTimePicker.setTimeInterval(interval)
        }

        binding.addTimeButton.setOnSingleClickListener {
            val startTime = String.format(
                "%02d:%02d",
                binding.startTimePicker.hour,
                binding.startTimePicker.minute * interval
            )
            val endTime = String.format(
                "%02d:%02d",
                binding.endTimePicker.hour,
                binding.endTimePicker.minute * interval
            )
            val timeRange = IntRange(
                stringTimeToInt(startTime),
                stringTimeToInt(endTime)
            )

            when {
                binding.startTimePicker.hour > binding.endTimePicker.hour -> {
                    Toast.makeText(requireContext(), invalidTimeMessage, Toast.LENGTH_SHORT).show()
                }

                binding.startTimePicker.hour < binding.endTimePicker.hour -> {
                    if (!checkIsDuplicateTime(timeRange)) {
                        addTime(startTime, endTime)
                    } else {
                        Toast.makeText(requireContext(), duplicateTimeMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                // 시작 시간과 종료 시간의 hour가 같은 경우
                else -> {
                    // 시작 시간의 분이 종료 시간의 분 보다 큰 경우
                    if (binding.startTimePicker.minute >= binding.endTimePicker.minute) {
                        Toast.makeText(requireContext(), invalidTimeMessage, Toast.LENGTH_SHORT)
                            .show()
                        return@setOnSingleClickListener
                    }
                    // 시작 시간의 분이 종료 시간의 분 보다 작은 경우
                    if (binding.startTimePicker.minute < binding.endTimePicker.minute) {
                        when (checkIsDuplicateTime(timeRange)) {
                            true -> Toast.makeText(requireContext(), duplicateTimeMessage, Toast.LENGTH_SHORT).show()
                            false -> addTime(startTime, endTime)
                        }
                    }
                }
            }
        }
    }

    private fun stringTimeToInt(time: String): Int {
        val (hours, minutes) = time.split(":").map { it.toInt() }
        Log.d(TAG, "stringTimeToInt: $hours $minutes")
        return hours * 60 + minutes
    }

    private fun addTime(startTime: String, endTime: String) {
        onTimeSelected(TimeTableDto(startTime, endTime))
        dismiss()
    }

    private fun checkIsDuplicateTime(newTime: IntRange): Boolean {
        return times.containsOverlap(newTime)
    }

    companion object {
        private const val TAG = "StartEndTimePickerDialog"
        private const val invalidTimeMessage = "시작 시간이 종료 시간보다 늦을 수 없습니다."
        private const val duplicateTimeMessage = "중복된 시간입니다."
    }
}