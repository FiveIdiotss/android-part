package com.minhoi.memento.ui.mypage

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
    private val myPostsAdapter: MyPostsAdapter by lazy { MyPostsAdapter(
        onBookmarkClickListener = {

        },
        onItemClickListener = {
            startActivity(Intent(this, BoardActivity::class.java).apply {
                putExtra("boardId", it)
            })
        },
    ) }

    override fun initView() {
        viewModel.getMemberBoards()

        binding.myPostsRv.apply {
            adapter = myPostsAdapter
            layoutManager = LinearLayoutManager(this@MyPostsActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
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
}