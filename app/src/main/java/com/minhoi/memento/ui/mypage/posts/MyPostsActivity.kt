package com.minhoi.memento.ui.mypage.posts

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityMyPostsBinding
import com.minhoi.memento.ui.common.dialog.SettingBottomSheetDialog
import com.minhoi.memento.ui.common.SettingOption
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.adapter.MyPostsAdapter
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.ui.mypage.MypageViewModel
import com.minhoi.memento.ui.common.BottomInfiniteScrollListener
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
            },
            onSettingClickListener = {
                SettingBottomSheetDialog().apply {
                    selectSetting(object :
                        SettingBottomSheetDialog.OnSettingValueClickListener {
                        override fun onSettingValueSelected(value: SettingOption) {
                            when (value) {
                                SettingOption.EDIT_POST -> {}
                                SettingOption.DELETE_POST -> {
                                }
                            }
                        }
                    })
                }.show(supportFragmentManager, "settingCategoryFragment")
            }
        )
    }

    override fun initView() {
        viewModel.getMemberBoards()
        setupToolbar("내가 작성한 게시글")

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
}