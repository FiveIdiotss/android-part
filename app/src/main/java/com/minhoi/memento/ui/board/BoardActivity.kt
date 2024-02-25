package com.minhoi.memento.ui.board

import androidx.activity.viewModels
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardBinding
import com.minhoi.memento.utils.setOnSingleClickListener

class BoardActivity : BaseActivity<ActivityBoardBinding>() {
    private val viewModel by viewModels<BoardViewModel>()
    override val layoutResourceId: Int = R.layout.activity_board

    override fun initView() {
        val boardId = intent.getLongExtra("boardId", -1L)
        getBoardContent(boardId)

        binding.mentorApplyBtn.setOnSingleClickListener {
            // 멘토링 신청 Dialog Show
            val bottomSheetDialog = MentoringApplyBottomSheetDialog()
            bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
        }
    }

    private fun getBoardContent(boardId: Long) {
        if (boardId != -1L) {
            viewModel.getBoardContent(boardId)
        }
        else {
            // 오류 처리
            finish()
        }
    }
}