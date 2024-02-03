package com.minhoi.memento.ui.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.BoardAdapter
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_home

    private val viewModel by viewModels<HomeViewModel>()
    private val boardAdapter: BoardAdapter by lazy { BoardAdapter() }

    override fun initView() {
        binding.recyclerView.apply {
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