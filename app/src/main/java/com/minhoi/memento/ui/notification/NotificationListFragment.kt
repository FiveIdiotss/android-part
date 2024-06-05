package com.minhoi.memento.ui.notification

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.data.model.NotificationListType
import com.minhoi.memento.databinding.FragmentNotificationsBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.adapter.NotificationListAdapter
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.ui.home.HomeViewModel
import com.minhoi.memento.ui.question.QuestionInfoActivity
import com.minhoi.memento.utils.BottomInfiniteScrollListener
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationListFragment : BaseFragment<FragmentNotificationsBinding>() {
    override val layoutResourceId: Int = R.layout.fragment_notifications
    private val viewModel by activityViewModels<HomeViewModel>()
    private val notificationListAdapter: NotificationListAdapter by lazy {
        NotificationListAdapter {
            when (it.type) {
                NotificationListType.APPLY -> {
                    startActivity(
                        Intent(
                            requireContext(),
                            BoardActivity::class.java
                        ).putExtra("boardId", it.otherPK)
                    )
                }

                NotificationListType.REPLY -> {
                    startActivity(
                        Intent(
                            requireContext(),
                            QuestionInfoActivity::class.java
                        ).putExtra("questionId", it.otherPK)
                    )
                }

                NotificationListType.MATCHING_DECLINE -> {}
                NotificationListType.MATCHING_COMPLETE -> {}
            }
        }
    }

    override fun initView() {
        viewModel.getNotificationList()
        observeNotificationList()

        binding.notificationRv.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            adapter = notificationListAdapter
            addOnScrollListener(BottomInfiniteScrollListener(
                layoutManager = linearLayoutManager,
                isLoading = { viewModel.notificationList.value is UiState.Loading },
                isLastPage = { viewModel.isLastPage },
                loadMore = { viewModel.getNotificationList() }
            ))
        }
    }

    private fun observeNotificationList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationList.collectLatest { state ->
                    when (state) {
                        is UiState.Empty, UiState.Loading -> {}
                        is UiState.Success -> {
                            notificationListAdapter.submitList(state.data)
                            parentFragmentManager.hideLoading()
                        }

                        is UiState.Error -> {
                            parentFragmentManager.hideLoading()
                            requireContext().showToast(state.error!!.message!!)
                        }
                    }
                }
            }
        }
    }
}