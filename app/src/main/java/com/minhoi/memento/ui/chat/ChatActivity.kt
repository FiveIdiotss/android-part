package com.minhoi.memento.ui.chat

import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhoi.memento.R
import com.minhoi.memento.adapter.ChatAdapter
import com.minhoi.memento.base.BaseActivity
import com.minhoi.memento.data.dto.chat.ChatDate
import com.minhoi.memento.data.dto.chat.ChatMessage
import com.minhoi.memento.databinding.ActivityChatBinding
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.hideLoading
import com.minhoi.memento.utils.setOnSingleClickListener
import com.minhoi.memento.utils.showLoading
import com.minhoi.memento.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {
    private val TAG = ChatActivity::class.java.simpleName
    override val layoutResourceId: Int = R.layout.activity_chat

    private var receiverId = -1L
    private val chatAdapter: ChatAdapter by lazy { ChatAdapter() {
        ChatImageViewerDialog.newInstance(it).show(supportFragmentManager, "imageDialog")
    } }
    private val viewModel by viewModels<ChatViewModel>()
    private var roomId = -1L
    private var hasNextPage: Boolean = true
    private var isFirstLoad = true

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
            layoutManager =
                LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
            addOnScrollListener(chatScrollListener)
        }

        binding.imageSelectBtn.setOnSingleClickListener {
            pickImageFromGallery()
        }

        /**
         * 채팅방의 채팅 목록을 관찰하여 초기 로딩 시에는 로딩 후 최하단으로 스크롤하고
         * 그 이후에 이전 메세지 불러와도 아래로 스크롤 되지 않도록 구현.
         * 소켓으로 전달된 단일 메세지 수신 시 최하단 스크롤.
         */
        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                val chatCount = chatAdapter.itemCount
                // 채팅방의 채팅 목록이 처음 로딩되거나, 실시간으로 새로운 메세지가 전달되었을 때 최하단으로 스크롤
                if (itemCount == 1 && (positionStart >= (chatCount - 1)) || isFirstLoad) {
                    isFirstLoad = false
                    binding.chatRv.post {
                        binding.chatRv.scrollToPosition(chatCount - 1)
                    }
                }
            }
        })

        setUpToolbar()
        observeChatMessages()
        observeHasNextPage()
        observePageLoadingState()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.chatToolbar)
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

    private val pickImageContract =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                val inputStream = contentResolver.openInputStream(uri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                inputStream?.copyTo(byteArrayOutputStream)
                val bytes = byteArrayOutputStream.toByteArray()

                val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)
                viewModel.sendImage("data:image/jpeg;base64,$base64String", roomId)
            }
        }

    private fun pickImageFromGallery() {
        pickImageContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun observeChatMessages() {
        viewModel.messages.observe(this) {
            val list = setShowProfileAndDate(it).toList()
            Log.d(TAG, "observeChatMessages: $list")
            chatAdapter.submitList(list)
        }
    }

    /*
    채팅 목록을 전달받아, 이전 메세지와 비교하여 날짜가 다르거나 사용자가 다르면 ChatMessage의 showDate를 true로 설정
     */
    private fun setShowProfileAndDate(messages: List<ChatMessage>): List<ChatMessage> {
        val resultMessages = mutableListOf<ChatMessage>()
        if (messages.isEmpty()) return resultMessages

        var lastMessage: ChatMessage? = null

        messages.forEachIndexed { index, currentMessage ->
            if (index == 0 || lastMessage!!.date.substring(0, 13) != currentMessage.date.substring(0, 13)) {
                val chatDate = ChatDate(currentMessage.id, currentMessage.name, currentMessage.content, currentMessage.date, currentMessage.image, true)
                resultMessages.add(chatDate)
            }

            // 이전 메시지와 현재 메시지가 같은 날짜인지 확인 (분 단위까지 비교)
            val isSameDay = lastMessage?.date?.substring(14, 22) == currentMessage.date.substring(14, 22)
            val isSameUser = lastMessage?.id == currentMessage.id

            currentMessage.showMinute = !isSameDay || !isSameUser
            resultMessages.add(currentMessage)

            lastMessage = currentMessage
        }
        return resultMessages
    }

    private fun observePageLoadingState() {
        lifecycleScope.launch {
            viewModel.isPageLoading.collectLatest {
                if (it is UiState.Error) {
                    showToast(LOAD_ERROR_MESSAGE)
                }
            }
        }
    }

    private fun loadPreviousPage() {
        if (roomId == -1L) {
            showToast(LOAD_ERROR_MESSAGE)
            return
        }
        if (viewModel.isPageLoading.value !is UiState.Loading) {
            viewModel.getMessageStream(roomId)
        }
    }

    private fun sendMessage() {
        if (roomId == -1L) {
            showToast(LOAD_ERROR_MESSAGE)
            return
        } else {
            viewModel.sendMessage(binding.message.text.toString(), roomId)
            binding.message.text.clear()
        }
    }

    /**
     * viewModel에서 roomId를 성공적으로 가져올 경우에만 소켓 연결하고, 방의 채팅 목록 일부를 가져오는 함수
     */
    private fun connectSocket() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.chatRoomState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            supportFragmentManager.showLoading()
                            binding.sendBtn.isEnabled = false
                        }
                        is UiState.Success -> {
                            supportFragmentManager.hideLoading()
                            binding.sendBtn.isEnabled = true
                            if (receiverId != -1L) {
                                Log.d(TAG, "connectSocket: ${state.data}")
                                roomId = state.data
                                viewModel.connectToWebSocket(state.data)
                                viewModel.getMessageStream(state.data)
                            } else {
                                showToast(LOAD_ERROR_MESSAGE)
                            }
                        }
                        is UiState.Error -> {
                            supportFragmentManager.hideLoading()
                            Log.d(TAG, "connectSocket: Error")
                            showToast(LOAD_ERROR_MESSAGE)
                        }
                        is UiState.Empty -> {}
                    }
                }
            }
        }
    }

    private fun observeHasNextPage() {
        viewModel.hasNextPage.observe(this) {
            hasNextPage = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }

    private val chatScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            // 스크롤 방향이 위로이고, 최상단에 도달했는지 여부를 확인
            if (dy < 0 && firstVisibleItemPosition == 0) {
                // 최상단에 도달하고 위로 스크롤 중이므로 새로운 데이터를 불러옴
                if (hasNextPage) {
                    loadPreviousPage()
                }
            }
        }
    }

    companion object {
        private const val LOAD_ERROR_MESSAGE = "채팅방을 불러오는데 실패했습니다. 다시 시도해주세요."
        private const val PERMISSION_CODE = 1001
    }
}