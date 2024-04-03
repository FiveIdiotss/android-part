package com.minhoi.memento.ui.board

import android.content.Intent
import com.minhoi.memento.adapter.BoardAdapter
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.BoardLoadStateAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardListBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BoardListActivity : BaseActivity<ActivityBoardListBinding>() {
    override val layoutResourceId: Int = R.layout.activity_board_list
    private lateinit var viewModel: BoardViewModel

    private val boardAdapter: BoardAdapter by lazy {
        BoardAdapter(
            onItemClickListener = {
                val intent = Intent(this, BoardActivity::class.java)
                intent.putExtra("boardId", it.boardId)
                startActivity(intent)
            },
            onBookmarkClickListener = {
                viewModel.executeBookmark(it.boardId, it.isBookmarked)
                Log.d("BookMarkClick", "${it}:clicked!!!!! ")
            }
        )
    }

    override fun initView() {
        viewModel = ViewModelProvider(this)[BoardViewModel::class.java]

        binding.apply {
            boardRv.adapter = boardAdapter.withLoadStateFooter(BoardLoadStateAdapter())
            boardRv.layoutManager =
                LinearLayoutManager(this@BoardListActivity, LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.boardList.collectLatest {
                    Log.d("ENDLESS", "onCreate:${it.toString()} ")
                    when (it) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            boardAdapter.submitData(it.data)
                        }
                        is UiState.Error -> {
                            showToast("게시글을 불러오는데 실패했습니다.")
                        }
                    }
                    viewModel.bookmarkState.collectLatest {
                        when (it) {
                            is UiState.Empty -> {}
                            is UiState.Loading -> {
                                supportFragmentManager.showLoading()
                                Log.d("bookmarkState", "Loading")
                            }

                            is UiState.Success -> {
                                supportFragmentManager.hideLoading()
                                boardAdapter.modify(it.data)
                                boardAdapter.refresh()
                                Log.d("bookmarkState", "Success")
                            }

                            is UiState.Error -> {
                                supportFragmentManager.hideLoading()
                                showToast("북마크 실패")
                                Log.d("bookmarkState", "Error")
                            }
                        }
                    }
                }
            }
        }
    }
}