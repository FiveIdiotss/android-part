package com.minhoi.memento.ui.board

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
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
        binding.viewModel = viewModel
        getBoardContent(boardId)
        setSupportActionBar(binding.toolbar)
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
        initScrollViewListener()
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
    private fun initScrollViewListener() {
        binding.boardScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            val scrollBounds = IntArray(2)
            v.getLocationOnScreen(scrollBounds)
            val scrollViewTop = scrollBounds[1]
            val scrollViewBottom = scrollViewTop + v.height

            binding.boardImage.getLocationOnScreen(scrollBounds)
            val imageViewTop = scrollBounds[1]
            val imageViewBottom = imageViewTop + binding.boardImage.height

            if (imageViewBottom > scrollViewTop && imageViewTop < scrollViewBottom) {
                // ImageView가 ScrollView 내에 보이는 경우
                val visiblePart = Math.min(imageViewBottom, scrollViewBottom) - Math.max(imageViewTop, scrollViewTop)
                val totalHeight = imageViewBottom - imageViewTop
                val percentageVisible = visiblePart.toFloat() / totalHeight.toFloat()
                val alpha = (percentageVisible * 255).toInt().coerceIn(0, 255)
                binding.toolbar.setBackgroundColor(Color.argb(alpha, 255, 255, 255))
            } else if (imageViewTop >= scrollViewBottom) {
                // ImageView가 아직 ScrollView 내에 보이지 않는 경우 (아래에 위치)
                binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            } else if (scrollY < imageViewTop - scrollViewTop) {
                // 사용자가 최상단으로 스크롤 했을 때 (ImageView 위로)
                binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            } else {
                // ImageView가 ScrollView를 넘어간 경우
                binding.toolbar.setBackgroundColor(Color.WHITE)
            }
        })
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