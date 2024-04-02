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
import com.minhoi.memento.utils.overlaps
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.setTimeInterval

class StartEndTimePickerDialog(
    private val interval: Int,
    private val onTimeSelected: (TimeTableDto) -> Unit
) : DialogFragment() {

    private lateinit var binding: StartEndTimepickerDialogBinding
    private val times = mutableListOf<IntRange>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.start_end_timepicker_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                binding.startTimePicker.hour + binding.startTimePicker.minute * interval,
                binding.endTimePicker.hour + binding.endTimePicker.minute * interval
            )
            Log.d("RANGE", "onViewCreated: $timeRange")

            when {
                binding.startTimePicker.hour > binding.endTimePicker.hour -> {
                    Toast.makeText(requireContext(), invalidTimeMessage, Toast.LENGTH_SHORT).show()
                }

                binding.startTimePicker.hour < binding.endTimePicker.hour -> {
                    if (!checkIsDuplicateTime(timeRange)) {
                        addTime(timeRange, startTime, endTime)
                        Log.d("TIMES", "onViewCreated: ${"times : $times"}")
                    } else {
                        Toast.makeText(requireContext(), duplicateTimeMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                else -> {
                    if (binding.startTimePicker.minute >= binding.endTimePicker.minute) {
                        Toast.makeText(requireContext(), invalidTimeMessage, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (!checkIsDuplicateTime(timeRange)) {
                            addTime(timeRange, startTime, endTime)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                duplicateTimeMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun addTime(timeRange: IntRange, startTime: String, endTime: String) {
        times.add(timeRange)
        onTimeSelected(TimeTableDto(startTime, endTime))
        dismiss()
    }

    private fun checkIsDuplicateTime(newTime: IntRange): Boolean {
        var isOverlapped = false
        for (time in times) {
            Log.d("RANGE", "check: ${time + newTime}")
            if (time.overlaps(newTime)) {
                isOverlapped = true
                break
            }
        }
        return isOverlapped
    }

    companion object {
        private const val invalidTimeMessage = "시작 시간이 종료 시간보다 늦을 수 없습니다."
        private const val duplicateTimeMessage = "중복된 시간입니다."
    }
}