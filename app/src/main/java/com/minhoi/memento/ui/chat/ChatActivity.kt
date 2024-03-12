package com.minhoi.memento.ui.chat

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhoi.memento.R
import com.minhoi.memento.adapter.ChatAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.databinding.ActivityChatBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<ActivityChatBinding>() {
    private val TAG = ChatActivity::class.java.simpleName
    override val layoutResourceId: Int = R.layout.activity_chat

    private var receiverId = -1L
    private val chatAdapter: ChatAdapter by lazy { ChatAdapter() }
    private val viewModel by viewModels<ChatViewModel>()

    override fun initView() {
        receiverId = intent.getLongExtra("receiverId", -1L)
        val receiverName = intent.getStringExtra("receiverName")
        // 채팅방 이름 설정
        binding.receiverName.text = receiverName

        Log.d(TAG, "initView: $receiverId")
        viewModel.getChatRoomId(receiverId)
        // viewModel roomId 관찰하여 roomId가 정상적으로 들어오면 소켓 연결
        connectSocket()

        binding.sendBtn.setOnSingleClickListener {
            sendMessage()
        }

        binding.chatRv.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.messages.observe(this) {
            Log.d(TAG, "initView: $it")
            chatAdapter.addMessage(it.last())
        }
    }

    private fun sendMessage() {
        viewModel.sendMessage(binding.message.text.toString())
        binding.message.text.clear()
    }

    private fun connectSocket() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatRoomState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {}
                        is UiState.Success<*> -> {
                            viewModel.connectToWebSocket(receiverId)
                        }
                        is UiState.Error -> {
                            showToast("채팅방을 불러오는데 실패했습니다. 다시 시도해주세요.")
                        }
                        is UiState.Empty -> {}
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }
}