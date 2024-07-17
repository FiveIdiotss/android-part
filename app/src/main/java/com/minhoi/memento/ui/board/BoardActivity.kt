package com.minhoi.memento.ui.board

import android.content.Intent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.adapter.board.BoardImagePagerAdapter
import com.minhoi.memento.ui.board.apply.ApplyMentoringActivity
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
    private val boardImagePagerAdapter: BoardImagePagerAdapter by lazy { BoardImagePagerAdapter() }

    override fun initView() {
        boardId = intent.getLongExtra("boardId", -1L)
        binding.viewModel = viewModel
        getBoardContent(boardId)
        setupToolbar("")

        binding.boardImagesViewPager.apply {
            adapter = boardImagePagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.boardImageDotsIndicator.attachTo(this)
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
        observeIsMyPost()
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
                            binding.boardContent = it.data.boardDTO
                            viewModel.setBookmarkState(it.data.boardDTO.isBookmarked)
                            boardImagePagerAdapter.submitList(it.data.boardImageUrls)
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

    private fun observeIsMyPost() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isMyPost.collectLatest {
                    if (it) {
                        binding.mentorApplyBtn.apply {
                            text = "내가 작성한 모집 글"
                            isClickable = false
                            background = ContextCompat.getDrawable(context, R.drawable.round_corner_gray_filled)
                        }
                    }
                }
            }
        }
    }
}