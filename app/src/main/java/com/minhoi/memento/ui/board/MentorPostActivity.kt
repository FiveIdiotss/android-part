package com.minhoi.memento.ui.board

import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.MentorBoardPostDto
import com.minhoi.memento.data.dto.TimeTableDto
import com.minhoi.memento.data.model.BoardType
import com.minhoi.memento.data.model.DayOfWeek
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.BoardService
import com.minhoi.memento.databinding.ActivityMentorPostBinding
import com.minhoi.memento.ui.StartEndTimePickerDialog
import com.minhoi.memento.ui.adapter.MentorTimeTableAdapter
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.launch

class MentorPostActivity : BaseActivity<ActivityMentorPostBinding>() {
    override val layoutResourceId: Int = R.layout.activity_mentor_post
    private lateinit var mentorTimeTableAdapter: MentorTimeTableAdapter
    private val selectedCheckBoxes = mutableSetOf<DayOfWeek>()
    private var consultTime: Int = 0
    private val retrofitClient = RetrofitClient.getLoggedInInstance().create(BoardService::class.java)
    private val timetables = mutableListOf<TimeTableDto>()

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
            timetables.removeAt(it)
            mentorTimeTableAdapter.submitList(timetables.toList())
        }

        binding.selectTimeTableLayout.setOnSingleClickListener {
            checkPickedTimeAndShowDialog()
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

            if (consultTime == 0) {
                showToast("상담 시간을 선택해주세요")
                return@setOnSingleClickListener
            }

            if (timetables.isEmpty()) {
                showToast("상담 가능한 시간대를 추가해주세요")
                return@setOnSingleClickListener
            }

            val post = MentorBoardPostDto(
                binding.inputTitle.text.toString(),
                binding.inputIntroduce.text.toString(),
                binding.inputTarget.text.toString(),
                binding.inputDescription.text.toString(),
                consultTime,
                BoardType.MENTEE,
                mentorTimeTableAdapter.currentList,
                selectedCheckBoxes.toList()
            )

            lifecycleScope.launch {
                val response = retrofitClient.writeMenteeBoard(post)
                if (response.isSuccessful) {
                    showToast("게시물이 등록되었습니다")
                    finish()
                } else {
                    showToast("일시적인 오류가 발생하였습니다. 다시 시도해주세요")
                    Log.d("TIMETALBEERROR", "initView: ${response.errorBody()}")
                }
            }
        }
        deleteTimeTableWhenTimeSelected()
    }

    // 30분/1시간 안누르면 시간 추가 못하게
    // 30분 -> 30분 단위로 시간 고를 수 있게, 1시간 -> 1시간 단위로 시간 고를 수 있게
    private fun checkPickedTimeAndShowDialog() {
        val selectedTimeId = binding.radioGroup.checkedRadioButtonId
        when (selectedTimeId) {
            binding.minutesRadioButton.id -> {
                consultTime = THIRTY_MINUTES
                showTimePickerDialog(THIRTY_MINUTES)
            }
            binding.hourRadioButton.id -> {
                consultTime = ONE_HOUR
                showTimePickerDialog(ONE_HOUR)
            }
            else -> showToast("시간을 선택해주세요")
        }
    }

    // 상담 시간이 변경되면 이전에 골랐던 시간표 제거 (30분 -> 1시간, 1시간 -> 30분으로 변경될 때)
    private fun deleteTimeTableWhenTimeSelected() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.minutesRadioButton, R.id.hourRadioButton -> {
                    timetables.clear()
                    mentorTimeTableAdapter.submitList(timetables.toList())
                }
            }
        }
    }

    private fun showTimePickerDialog(interval: Int) {
        val timePickerDialog = StartEndTimePickerDialog(interval, timetables) {
            timetables.add(it)
            // submitList는 list의 주소가 같으면 변경사항이 없다고 판단하기 때문에 새로운 List 넘기기
            mentorTimeTableAdapter.submitList(ArrayList(timetables))
        }
        timePickerDialog.show(supportFragmentManager, "StartEndTimePickerDialog")
    }

    companion object {
        private const val TAG = "MentorPostActivity"
        const val THIRTY_MINUTES = 30
        const val ONE_HOUR = 60
    }
}