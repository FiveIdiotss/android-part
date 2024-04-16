package com.minhoi.memento.ui.mypage

import android.content.Intent
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.MyPostsAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityMyPostsBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyPostsActivity : BaseActivity<ActivityMyPostsBinding>() {
    override val layoutResourceId: Int = R.layout.activity_my_posts
    private val viewModel by viewModels<MypageViewModel>()
    private val myPostsAdapter: MyPostsAdapter by lazy {
        MyPostsAdapter(
            onItemClickListener = {
                startActivity(Intent(this, BoardActivity::class.java).apply {
                    putExtra("boardId", it)
                })
            },
            onBookmarkClickListener = { boardContent, position ->
                viewModel.executeBookmarkInList(boardContent.boardId, boardContent.isBookmarked)
                val s = myPostsAdapter.currentList.toMutableList()
                s.set(position, boardContent.copy(isBookmarked = !boardContent.isBookmarked))
                myPostsAdapter.submitList(s)
            }
        )
    }

    override fun initView() {
        viewModel.getMemberBoards()
        binding.toolbar.apply {
            setTitle("내가 작성한 게시글")
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        binding.myPostsRv.apply {
            adapter = myPostsAdapter
            layoutManager = LinearLayoutManager(this@MyPostsActivity, LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.memberBoards.collectLatest { state ->
                    when (state) {
                        UiState.Empty -> {}
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            myPostsAdapter.submitList(state.data)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}