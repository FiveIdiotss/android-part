package com.minhoi.memento.ui.board

import android.content.Intent
import com.minhoi.memento.adapter.BoardAdapter
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.BoardLoadStateAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityBoardListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BoardListActivity : BaseActivity<ActivityBoardListBinding>() {
    override val layoutResourceId: Int = R.layout.activity_board_list
    private lateinit var viewModel: BoardViewModel

    private val boardAdapter: BoardAdapter by lazy { BoardAdapter {
        // onItemClickListener
        startActivity(Intent(this, BoardActivity::class.java).apply {
            putExtra("boardId", it.boardId)
        })
    } }

    override fun initView() {
        viewModel = ViewModelProvider(this)[BoardViewModel::class.java]
        binding.apply {
            boardRv.adapter = boardAdapter.withLoadStateFooter(BoardLoadStateAdapter())
            boardRv.layoutManager = LinearLayoutManager(this@BoardListActivity, LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            viewModel.getBoardsStream.collectLatest {
                Log.d("ENDLESS", "onCreate:${it.toString()} ")
                boardAdapter.submitData(it)
            }
        }
    }
}