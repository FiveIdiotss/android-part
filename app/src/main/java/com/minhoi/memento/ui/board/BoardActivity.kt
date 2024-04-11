package com.minhoi.memento.ui.board

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BoardActivity : BaseActivity<ActivityBoardBinding>() {
    private val viewModel by viewModels<BoardViewModel>()
    private var boardId: Long = -1
    override val layoutResourceId: Int = R.layout.activity_board

    override fun initView() {
        boardId = intent.getLongExtra("boardId", -1L)
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
        observeBookmarkState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.board_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_bookmark -> {
                viewModel.boardBookmarkState.value?.let { viewModel.executeBookmark(boardId, it) }
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
                binding.toolbar.menu.findItem(R.id.action_bookmark).icon =
                    ContextCompat.getDrawable(this, R.drawable.heart_filled)
            } else {
                binding.toolbar.menu.findItem(R.id.action_bookmark).icon =
                    ContextCompat.getDrawable(this, R.drawable.heart_empty)
            }
        }
    }
}