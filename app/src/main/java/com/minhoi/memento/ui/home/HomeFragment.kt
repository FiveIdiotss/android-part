package com.minhoi.memento.ui.home

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.BoardPreviewAdapter
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentHomeBinding
import com.minhoi.memento.ui.board.BoardListActivity
import com.minhoi.memento.utils.setOnSingleClickListener

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_home

    private val viewModel by viewModels<HomeViewModel>()
    private val boardAdapter: BoardPreviewAdapter by lazy { BoardPreviewAdapter() }

    override fun initView() {

        binding.toMenteeBoard.setOnSingleClickListener {
            startActivity(Intent(requireContext(), BoardListActivity::class.java))
        }

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        observeMenteeBoard()
    }

    private fun observeMenteeBoard() {
        viewModel.menteeBoardContent.observe(viewLifecycleOwner) { contents ->
            boardAdapter.setList(contents)
        }
    }

}