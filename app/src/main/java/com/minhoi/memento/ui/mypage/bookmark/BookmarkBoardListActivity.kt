package com.minhoi.memento.ui.mypage.bookmark

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBookmarkBoardListBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.adapter.MyPostsAdapter
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.ui.mypage.MypageViewModel
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarkBoardListActivity : BaseActivity<ActivityBookmarkBoardListBinding>() {
    override val layoutResourceId: Int = R.layout.activity_bookmark_board_list
    private val viewModel by viewModels<MypageViewModel>()
    private val bookmarkAdapter: MyPostsAdapter by lazy {
        MyPostsAdapter(
            onItemClickListener = {
                startActivity(Intent(this, BoardActivity::class.java).apply {
                    putExtra("boardId", it)
                })
            },
            onBookmarkClickListener = { boardContent, position ->
                viewModel.executeBookmarkInList(boardContent.boardId, boardContent.isBookmarked)
                val currentList = bookmarkAdapter.currentList.toMutableList()
                currentList[position] = boardContent.copy(isBookmarked = !boardContent.isBookmarked)
                bookmarkAdapter.submitList(currentList)
            },
            onSettingClickListener = {}
        )
    }

    override fun initView() {
        setupToolbar("북마크한 게시글")
        viewModel.getBookmarkBoards()
        binding.bookmarkBoardsRv.apply {
            adapter = bookmarkAdapter
            layoutManager = LinearLayoutManager(this@BookmarkBoardListActivity, LinearLayoutManager.VERTICAL, false)
        }
        observeBookmarkBoards()
    }

    private fun observeBookmarkBoards() {
        lifecycleScope.launch {
            viewModel.bookmarkBoards.collectLatest { state ->
                when (state) {
                    is UiState.Empty -> {}
                    is UiState.Loading -> {
                        supportFragmentManager.showLoading()
                    }

                    is UiState.Success -> {
                        supportFragmentManager.hideLoading()
                        bookmarkAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        supportFragmentManager.hideLoading()
                        showToast(state.error?.message.toString())
                    }
                }
            }
        }
    }
}