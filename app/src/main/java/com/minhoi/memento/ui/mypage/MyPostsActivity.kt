package com.minhoi.memento.ui.mypage

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.MyPostsAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityMyPostsBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.utils.BottomInfiniteScrollListener
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPostsActivity : BaseActivity<ActivityMyPostsBinding>() {
    override val layoutResourceId: Int = R.layout.activity_my_posts
    private val viewModel by viewModels<MypageViewModel>()
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val myPostsAdapter: MyPostsAdapter by lazy {
        MyPostsAdapter(
            onItemClickListener = {
                startActivity(Intent(this, BoardActivity::class.java).apply {
                    putExtra("boardId", it)
                })
            },
            onBookmarkClickListener = { boardContent, position ->
                viewModel.executeBookmarkInList(boardContent.boardId, boardContent.isBookmarked)
                val currentList = myPostsAdapter.currentList.toMutableList()
                currentList[position] = boardContent.copy(isBookmarked = !boardContent.isBookmarked)
                myPostsAdapter.submitList(currentList)
            }
        )
    }

    override fun initView() {
        viewModel.getMemberBoards()
        setUpToolbar()

        binding.myPostsRv.apply {
            adapter = myPostsAdapter
            layoutManager = linearLayoutManager
            addOnScrollListener(BottomInfiniteScrollListener(
                layoutManager = linearLayoutManager,
                isLoading = { viewModel.memberBoards.value is UiState.Loading },
                isLastPage = { viewModel.isLastPage },
                loadMore = { viewModel.getMemberBoards() }
            ))
        }

        lifecycleScope.launch {
            viewModel.memberBoards.collectLatest { state ->
                when (state) {
                    is UiState.Empty, UiState.Loading -> {}
                    is UiState.Success -> {
                        Log.d("MyPostsActivity", "initView: $state ")
                        myPostsAdapter.submitList(state.data)
                    }

                    is UiState.Error -> {
                        showToast(state.error?.message.toString())
                    }
                }
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
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
}