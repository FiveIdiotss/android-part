package com.minhoi.memento.ui.mypage

import android.content.Intent
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.MyPostsAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBookmarkBoardListBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast

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
                val s = bookmarkAdapter.currentList.toMutableList()
                s.set(position, boardContent.copy(isBookmarked = !boardContent.isBookmarked))
                bookmarkAdapter.submitList(s)
            }
        )
    }

    override fun initView() {
        setUpToolbar()
        viewModel.getBookmarkBoards()
        binding.bookmarkBoardsRv.apply {
            adapter = bookmarkAdapter
            layoutManager = LinearLayoutManager(this@BookmarkBoardListActivity, LinearLayoutManager.VERTICAL, false)
        }
        observeBookmarkBoards()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.bookmarkBoardsToolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
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

    private fun observeBookmarkBoards() {
        viewModel.bookmarkBoards.observe(this) { state ->
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