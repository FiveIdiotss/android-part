package com.minhoi.memento.ui.board

import android.util.Log
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
        showBoardContent(boardId)

        binding.mentorApplyBtn.setOnSingleClickListener {
            // 멘토 지원하기
        }
    }

    private fun showBoardContent(boardId: Long) {
        if (boardId != -1L) {
            viewModel.getBoardContent(boardId)
        }
        else {
            // 오류 처리
            finish()
        }
    }
}