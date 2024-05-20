package com.minhoi.memento.ui.board

import android.content.Intent
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardActivity : BaseActivity<ActivityBoardBinding>() {
    private val viewModel by viewModels<BoardViewModel>()
    private var boardId: Long = -1
    override val layoutResourceId: Int = R.layout.activity_board

    override fun initView() {
        boardId = intent.getLongExtra("boardId", -1L)
        binding.viewModel = viewModel
        getBoardContent(boardId)
        setSupportActionBar(binding.boardToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        binding.mentorApplyBtn.setOnSingleClickListener {
            // 멘토링 신청 Activity로 전환
            startActivity(Intent(this, ApplyMentoringActivity::class.java).apply {
                putExtra("boardId", viewModel.post.value?.boardDTO?.boardId)
            })
        }

        binding.bookmarkBtn.setOnSingleClickListener {
            viewModel.boardBookmarkState.value?.let { viewModel.executeBookmark(boardId, it) }
        }
        observeBoardContent()
        observeBookmarkState()
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

    private fun observeBookmarkState() {
        viewModel.boardBookmarkState.observe(this) { state ->
            if (state) {
                binding.bookmarkBtn.setImageResource(R.drawable.heart_filled)
            } else {
                binding.bookmarkBtn.setImageResource(R.drawable.heart_empty)
            }
        }
    }
}