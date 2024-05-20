package com.minhoi.memento.ui.board

import android.content.Intent
import com.minhoi.memento.adapter.BoardAdapter
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.BoardLoadStateAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardListBinding
import com.minhoi.memento.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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

        setUpToolbar()
        category = intent.getStringExtra("category")
        category?.let { binding.filterCategoryBtn.text = category }
        viewModel.setCategoryFilter(category)

        binding.apply {
            boardRv.adapter = boardAdapter.withLoadStateFooter(BoardLoadStateAdapter())
            boardRv.layoutManager =
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
                viewModel.getFilterBoardStream().collectLatest {
                    boardAdapter.submitData(it)
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