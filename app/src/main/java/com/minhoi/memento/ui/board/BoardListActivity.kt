package com.minhoi.memento.ui.board

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardListBinding
import com.minhoi.memento.ui.SelectBoardCategoryBottomSheetDialog
import com.minhoi.memento.ui.adapter.BoardAdapter
import com.minhoi.memento.ui.adapter.BoardLoadStateAdapter
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
        setUpToolbar()
        category = intent.getStringExtra("category")
        category?.let { binding.filterCategoryBtn.text = category }
        viewModel.setCategoryFilter(category)
        binding.emptyText = "게시글이 존재하지 않아요"

        boardAdapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                // 로딩 중 일 때
                progressBar.isVisible = combinedLoadStates.source.refresh is LoadState.Loading

                // 로딩 중이지 않을 때 (활성 로드 작업이 없고 에러가 없음)
                boardRv.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading

                // 로딩 에러 발생 시
                retryLayout.isVisible = combinedLoadStates.source.refresh is LoadState.Error

                if (combinedLoadStates.append.endOfPaginationReached) {
                    // 아이템 수가 1보다 작으면 Empty View 보이도록 처리
                    if (boardAdapter.itemCount < 1) {
                        binding.emptyLayout.root.visibility = View.VISIBLE
                    } else {
                        // 아이템 수가 1보다 크면 Empty View 안보이도록 처리
                        binding.emptyLayout.root.visibility = View.GONE
                    }
                }
            }
        }

        binding.boardRv.apply {
            adapter = boardAdapter.withLoadStateFooter(BoardLoadStateAdapter())
            layoutManager =
                LinearLayoutManager(this@BoardListActivity, LinearLayoutManager.VERTICAL, false)
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

    private fun setUpToolbar() {
        setSupportActionBar(binding.boardListToolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
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
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_search -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}