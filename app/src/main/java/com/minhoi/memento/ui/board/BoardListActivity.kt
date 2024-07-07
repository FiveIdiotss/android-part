package com.minhoi.memento.ui.board

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardListBinding
import com.minhoi.memento.ui.SelectBoardCategoryBottomSheetDialog
import com.minhoi.memento.ui.adapter.BoardAdapter
import com.minhoi.memento.ui.adapter.BoardLoadStateAdapter
import com.minhoi.memento.ui.handleLoadState
import com.minhoi.memento.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardListActivity : BaseActivity<ActivityBoardListBinding>() {
    override val layoutResourceId: Int = R.layout.activity_board_list
    private val viewModel by viewModels<BoardListViewModel>()
    private var category: String? = null
    private val boardAdapter: BoardAdapter by lazy {
        BoardAdapter(
            onItemClickListener = {
                val intent = Intent(this, BoardActivity::class.java)
                intent.putExtra("boardId", it.boardId)
                startActivity(intent)
            },
            onBookmarkClickListener = {}
        )
    }

    override fun initView() {
        binding.viewModel = viewModel
        setupToolbar("멘토링 모집 게시글")
        category = intent.getStringExtra("category")
        category?.let { binding.filterCategoryBtn.text = category }
        viewModel.setCategoryFilter(category)
        binding.emptyText = "게시글이 존재하지 않아요"

        boardAdapter.addLoadStateListener { combinedLoadStates ->
            handleLoadState(
                adapter = boardAdapter,
                loadState = combinedLoadStates,
                progressBar = binding.progressBar,
                recyclerView = binding.boardRv,
                retryLayout = binding.retryLayout,
                emptyLayout = binding.emptyLayout
            )
        }

        binding.boardRv.apply {
            adapter = boardAdapter.withLoadStateFooter(BoardLoadStateAdapter())
            layoutManager =
                LinearLayoutManager(this@BoardListActivity, LinearLayoutManager.VERTICAL, false)
        }

        binding.retryLayout.retryButton.setOnSingleClickListener {
            boardAdapter.retry()
        }

        binding.filterCategoryBtn.setOnSingleClickListener {
            SelectBoardCategoryBottomSheetDialog().apply {
                selectCategory(object :
                    SelectBoardCategoryBottomSheetDialog.OnCategorySelectedListener {
                    override fun onCategorySelected(category: String) {
                        binding.filterCategoryBtn.text = category
                        when (category) {
                            "모든 단과대" -> viewModel.setCategoryFilter(null)
                            else -> viewModel.setCategoryFilter(category)
                        }
                    }
                })
            }.show(supportFragmentManager, "selectCategoryFragment")
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchQueryFlow
                    .flatMapLatest { query ->
                        if (query.isNullOrBlank()) {
                            // 검색어가 없는 경우 기본 데이터 스트림 구독
                            viewModel.getFilterBoardStream()
                        } else {
                            // 검색어가 있는 경우 검색 데이터 스트림 구독
                            viewModel.getFilterBoardBySearch()
                        }
                    }
                    .collectLatest { pagingData ->
                        boardAdapter.submitData(pagingData)
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.boardlist_toolbar_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 입력 필드의 텍스트가 변경될 때마다 호출
                viewModel.setSearchQuery(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}