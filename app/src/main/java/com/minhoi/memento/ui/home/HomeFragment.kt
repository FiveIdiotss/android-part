package com.minhoi.memento.ui.home

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentHomeBinding
import com.minhoi.memento.ui.adapter.BoardAdapter
import com.minhoi.memento.ui.adapter.question.QuestionPreviewAdapter
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.ui.board.BoardListActivity
import com.minhoi.memento.ui.question.QuestionInfoActivity
import com.minhoi.memento.ui.question.QuestionListActivity
import com.minhoi.memento.ui.common.ColumnItemDecoration
import com.minhoi.memento.utils.setOnSingleClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_home

    private val viewModel by activityViewModels<HomeViewModel>()
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var questionPreviewAdapter: QuestionPreviewAdapter

    override fun initView() {

        boardAdapter = BoardAdapter(
            onItemClickListener = {
                startActivity(Intent(requireContext(), BoardActivity::class.java).putExtra(
                    "boardId", it.boardId
                ))
            },
            onBookmarkClickListener = {}
        )

        questionPreviewAdapter = QuestionPreviewAdapter {
            startActivity(Intent(requireContext(), QuestionInfoActivity::class.java).putExtra("questionId", it))
        }

        binding.toMentorBoard.setOnSingleClickListener {
            startActivity(Intent(requireContext(), BoardListActivity::class.java))
        }
        binding.toMenteeBoard.setOnSingleClickListener {
            startActivity(Intent(requireContext(), QuestionListActivity::class.java).putExtra("type", "wholeQuestion"))
        }

        binding.mentorBoardRv.apply {
            setHasFixedSize(true)
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        binding.questionRv.apply {
            setHasFixedSize(true)
            adapter = questionPreviewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(ColumnItemDecoration(requireContext(), 10))
        }
        observeMentorBoards()
        observeQuestionBoards()
    }

    private fun observeMentorBoards() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getPreviewBoards().collectLatest {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    boardAdapter.submitData(it)
                }
            }
        }
    }

    private fun observeQuestionBoards() {
        viewModel.questionPreviewContents.observe(viewLifecycleOwner) {
            questionPreviewAdapter.submitList(it)
        }
    }

}