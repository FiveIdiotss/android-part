package com.minhoi.memento.ui.board

import android.content.Intent
import android.view.MenuItem
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        binding.mentorApplyBtn.setOnSingleClickListener {
            // 멘토링 신청 Dialog Show
//            val bottomSheetDialog = MentoringApplyBottomSheetDialog()
//            bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
            startActivity(Intent(this, ApplyMentoringActivity::class.java))
        }
        observeBoardContent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

    private fun observeBoardContent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.boardContent.collectLatest {
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            binding.boardContent = it.data
                        }
                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            showToast(it.error!!.message!!)
                        }
                    }
                }
            }
        }
    }
}