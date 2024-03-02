package com.minhoi.memento.ui

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.MentorTimeTableAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.MentorBoardPostDto
import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.databinding.ActivityMentorPostBinding
import com.minhoi.memento.utils.BoardType
import com.minhoi.memento.utils.DayOfWeek
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.launch

class MentorPostActivity : BaseActivity<ActivityMentorPostBinding>() {
    override val layoutResourceId: Int = R.layout.activity_mentor_post
    private lateinit var mentorTimeTableAdapter: MentorTimeTableAdapter
    private val selectedCheckBoxes = mutableSetOf<DayOfWeek>()
    private var consultTime: Int = 0
    private val retrofitClient = RetrofitClient.getLoggedInInstance().create(APIService::class.java)
    private var timePickerDialog: StartEndTimePickerDialog? = null

    override fun initView() {
        val checkBoxes = listOf(
            binding.mondayCheckBox,
            binding.tuesdayCheckBox,
            binding.wednesdayCheckBox,
            binding.thursdayCheckBox,
            binding.fridayCheckBox,
            binding.saturdayCheckBox,
            binding.sundayCheckBox
        )

        mentorTimeTableAdapter = MentorTimeTableAdapter {
            // onDeleteClickListener
        }
        // 30분/1시간 안누르면 시간 추가 못하게
        // 30분 -> 30분 단위로 시간 고를 수 있게, 1시간 -> 1시간 단위로 시간 고를 수 있게
        binding.addTimeTableButton.setOnSingleClickListener {
            val selectedTimeId = binding.radioGroup.checkedRadioButtonId
            when (selectedTimeId) {
                binding.minutesRadioButton.id -> showTimePickerDialog(THIRTY_MINUTES)
                binding.hourRadioButton.id -> showTimePickerDialog(ONE_HOUR)
                else -> showToast("시간을 선택해주세요")
            }
        }

        binding.mentorTimeTableRv.apply {
            adapter = mentorTimeTableAdapter
            layoutManager = GridLayoutManager(this@MentorPostActivity, 2)
        }

        binding.postButton.setOnSingleClickListener {
            // 게시물 등록
            selectedCheckBoxes.clear()

            for ((index, checkBox) in checkBoxes.withIndex()) {
                if (checkBox.isChecked) {
                    val dayOfWeek = DayOfWeek.values()[index]
                    selectedCheckBoxes.add(dayOfWeek)
                }
            }

            if (selectedCheckBoxes.isEmpty()) {
                showToast("요일을 선택해주세요")
                return@setOnSingleClickListener
            }

            val timeTables = mentorTimeTableAdapter.getTimeTables()
            val s = MentorBoardPostDto("test", "test", 30,BoardType.MENTEE,timeTables,selectedCheckBoxes.toList())
            Log.d("TIMETABLE", "initView: ${s.times}")
            lifecycleScope.launch {
                val response = retrofitClient.writeMenteeBoard(s)
                if (response.isSuccessful) {
                    showToast("게시물이 등록되었습니다")
                } else {
                    showToast("게시물 등록에 실패했습니다")
                    Log.d("TIMETALBEERROR", "initView: ${response.errorBody()}")
                }
            }
        }
    }

    private fun showTimePickerDialog(interval: Int) {
        if (timePickerDialog == null) {
            timePickerDialog = StartEndTimePickerDialog(interval) {
                mentorTimeTableAdapter.addTimeTable(it)
            }
        }
        timePickerDialog!!.show(supportFragmentManager, "StartEndTimePickerDialog")
    }

    companion object {
        const val THIRTY_MINUTES = 30
        const val ONE_HOUR = 60
    }
}