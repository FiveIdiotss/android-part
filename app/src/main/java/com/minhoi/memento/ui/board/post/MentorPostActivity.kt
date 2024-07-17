package com.minhoi.memento.ui.board.post

import android.annotation.SuppressLint
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.model.DayOfWeek
import com.minhoi.memento.databinding.ActivityMentorPostBinding
import com.minhoi.memento.ui.common.dialog.SelectBoardCategoryBottomSheetDialog
import com.minhoi.memento.ui.common.dialog.StartEndTimePickerDialog
import com.minhoi.memento.ui.adapter.AddPhotoAdapter
import com.minhoi.memento.ui.adapter.MentorTimeTableAdapter
import com.minhoi.memento.ui.board.BoardViewModel
import com.minhoi.memento.utils.repeatOnStarted
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MentorPostActivity : BaseActivity<ActivityMentorPostBinding>() {
    override val layoutResourceId: Int = R.layout.activity_mentor_post
    private val viewModel by viewModels<BoardViewModel>()
    private val mentorTimeTableAdapter: MentorTimeTableAdapter by lazy {
        MentorTimeTableAdapter {
            // onDeleteClickListener
            viewModel.deleteMentorTimeTableAt(it)
        }
    }
    private val addPhotoAdapter: AddPhotoAdapter by lazy {
        AddPhotoAdapter(onDeleteClickListener = {
            viewModel.removePostImageAt(it)
        })
    }

    override fun initView() {
        binding.viewModel = viewModel

        val checkBoxes = listOf(
            binding.mondayCheckBox,
            binding.tuesdayCheckBox,
            binding.wednesdayCheckBox,
            binding.thursdayCheckBox,
            binding.fridayCheckBox,
            binding.saturdayCheckBox,
            binding.sundayCheckBox
        )

        checkBoxes.forEachIndexed { index, checkBox ->
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val dayOfWeek = DayOfWeek.values()[index]
                viewModel.setSelectedCheckBoxes(dayOfWeek, isChecked)
            }
        }

        val pickImageContract =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
                if (uris.isNotEmpty() && uris != null) {
                    viewModel.addPostImages(uris)
                }
            }

        binding.imageRv.apply {
            adapter = addPhotoAdapter
            layoutManager = LinearLayoutManager(this@MentorPostActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.closeBtn.setOnSingleClickListener {
            finish()
        }

        binding.selectTimeTableLayout.setOnSingleClickListener {
            checkPickedTimeAndShowDialog()
        }

        binding.mentorTimeTableRv.apply {
            adapter = mentorTimeTableAdapter
            layoutManager = GridLayoutManager(this@MentorPostActivity, 2)
        }

        binding.selectCategoryLayout.setOnSingleClickListener {
            SelectBoardCategoryBottomSheetDialog().apply {
                selectCategory(object :
                    SelectBoardCategoryBottomSheetDialog.OnCategorySelectedListener {
                    override fun onCategorySelected(category: String) {
                        viewModel.setCategory(category)
                        binding.mentoringCategory.text = category
                    }
                })
            }.show(supportFragmentManager, "selectCategoryFragment")
        }

        binding.addImageBtn.setOnSingleClickListener {
            pickImageContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.postButton.setOnSingleClickListener {
            // 게시물 등록
            if (viewModel.postAvailableDays.value.isEmpty()) {
                showToast("요일을 선택해주세요")
                return@setOnSingleClickListener
            }

            if (viewModel.postConsultTime.value == 0) {
                showToast("상담 시간을 선택해주세요")
                return@setOnSingleClickListener
            }

            if (viewModel.postTimeTable.value.isEmpty()) {
                showToast("상담 가능한 시간대를 추가해주세요")
                return@setOnSingleClickListener
            }

            if (viewModel.postCategory.value.isEmpty()) {
                showToast("멘토링 대상 단과대를 선택해주세요")
                return@setOnSingleClickListener
            }
            viewModel.postBoard()
        }
        deleteTimeTableWhenTimeSelected()
        observeTimeTable()
        observePostImages()
    }

    // 30분/1시간 안누르면 시간 추가 못하게
    // 30분 -> 30분 단위로 시간 고를 수 있게, 1시간 -> 1시간 단위로 시간 고를 수 있게
    private fun checkPickedTimeAndShowDialog() {
        val selectedTimeId = binding.radioGroup.checkedRadioButtonId
        when (selectedTimeId) {
            binding.minutesRadioButton.id -> {
                viewModel.setConsultTime(THIRTY_MINUTES)
                showTimePickerDialog(THIRTY_MINUTES)
            }
            binding.hourRadioButton.id -> {
                viewModel.setConsultTime(ONE_HOUR)
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
                    viewModel.changeConsultTime()
                }
            }
        }
    }

    private fun showTimePickerDialog(interval: Int) {
        val timePickerDialog = StartEndTimePickerDialog(interval, viewModel.postTimeTable.value) {
            viewModel.setMentorTimeTableList(it)
        }
        timePickerDialog.show(supportFragmentManager, "StartEndTimePickerDialog")
    }

    @SuppressLint("SetTextI18n")
    private fun observePostImages() {
        repeatOnStarted {
            viewModel.postImages.collect { images ->
                binding.imageCount.text = "${images.count()}/10"
                addPhotoAdapter.submitList(images.map { it.toString() })
            }
        }
    }

    private fun observeTimeTable() {
        repeatOnStarted {
            viewModel.postTimeTable.collect {
                mentorTimeTableAdapter.submitList(it)
            }
        }
    }

    private fun observePostEvent() {
        repeatOnStarted {

        }
    }

    companion object {
        private const val TAG = "MentorPostActivity"
        const val THIRTY_MINUTES = 30
        const val ONE_HOUR = 60
    }
}