package com.minhoi.memento.ui.chat

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.base.BaseFragment
import com.minhoi.memento.databinding.FragmentChatListBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.ui.adapter.ChatRoomListAdapter
import com.minhoi.memento.ui.home.HomeViewModel
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>() {

    override val layoutResourceId: Int = R.layout.fragment_chat_list
    private val viewModel by viewModels<HomeViewModel>()
    private val chatRoomListAdapter: ChatRoomListAdapter by lazy {
        ChatRoomListAdapter {
            // onCLickListener
            startActivity(Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra("receiverId", it.receiverId)
                putExtra("receiverName", it.receiverName)
            })
        }
    }

    override fun initView() {
        observeChatRooms()
        binding.chatRoomRv.apply {
            adapter = chatRoomListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    /*
    * ChatRoom을 관찰하여 성공적으로 데이터를 가져오면 ChatRoomListAdapter에 데이터를 전달, 실패하면 네트워크 오류 메시지를 띄우는 함수
     */
    private fun observeChatRooms() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatRooms.collectLatest { state ->
                    when (state) {
                        is UiState.Empty -> {}
                        is UiState.Loading -> {
                            parentFragmentManager.showLoading()
                        }
                        is UiState.Success -> {
                            parentFragmentManager.hideLoading()
                            Log.d("ChatRooms", "observeChatRooms: ${state.data}")
                            chatRoomListAdapter.setList(state.data)
                        }
                        is UiState.Error -> {
                            parentFragmentManager.hideLoading()
                            requireContext().showToast("네트워크 오류가 발생하였습니다. 다시 시도해주세요")
                        }
                    }
                }
            }
        }
    }
}