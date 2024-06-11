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
import com.minhoi.memento.ui.adapter.NotificationAdapter
import com.minhoi.memento.ui.board.BoardActivity
import com.minhoi.memento.ui.home.HomeViewModel
import com.minhoi.memento.ui.question.QuestionInfoActivity
import com.minhoi.memento.utils.setOnSingleClickListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationListFragment : BaseFragment<FragmentNotificationsBinding>() {
    override val layoutResourceId: Int = R.layout.fragment_notifications
    private val viewModel by activityViewModels<HomeViewModel>()
    private val notificationListAdapter: NotificationAdapter by lazy {
        NotificationAdapter(
            onItemClickListener = {
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
            },
            onDeleteClickListener = {
                viewModel.deleteNotification(it)
            }
        )
    }

    override fun initView() {
        viewModel.getNotifications()
        observeNotificationList()
        observeExpandState()

        binding.editNotificationBtn.setOnSingleClickListener {
            showDeleteNotificationButton()
        }

        binding.notificationRv.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            adapter = notificationListAdapter
        }
    }

    private fun showDeleteNotificationButton() {
        when (viewModel.expandState.value) {
            true -> viewModel.unExpandNotifications()
            false -> viewModel.expandNotifications()
        }
    }

    private fun observeExpandState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.expandState.collectLatest {
                    when (it) {
                        true -> binding.editNotificationBtn.text = "완료"
                        false -> binding.editNotificationBtn.text = "편집"
                    }
                }
            }
        }
    }

    private fun observeNotificationList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notifications.collectLatest { state ->
                    notificationListAdapter.submitData(state)
                }
            }
        }
    }
}