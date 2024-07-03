package com.minhoi.memento.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.chat.ChatMessage
import com.minhoi.memento.data.dto.chat.ChatRoom
import com.minhoi.memento.data.dto.chat.MessageDto
import com.minhoi.memento.data.model.MentoringExtendStatus
import com.minhoi.memento.data.network.GsonClient
import com.minhoi.memento.data.network.SaveFileResult
import com.minhoi.memento.data.network.socket.StompManager
import com.minhoi.memento.repository.chat.ChatRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.FileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONObject
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val fileManager: FileManager
) : ViewModel() {

    private val TAG = ChatViewModel::class.java.simpleName
    private val member = MentoApplication.memberPrefs.getMemberPrefs()
    private var currentPage: Int = 1
    private val _hasNextPage = MutableLiveData<Boolean>(true)
    private var roomId = -1L
    val hasNextPage: LiveData<Boolean> = _hasNextPage

    private val _connectState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val connectState: StateFlow<UiState<Boolean>> = _connectState.asStateFlow()

    private val _chatRoomState = MutableStateFlow<UiState<ChatRoom>>(UiState.Loading)
    val chatRoomState: StateFlow<UiState<ChatRoom>> = _chatRoomState.asStateFlow()

    private val _messages = MutableStateFlow<ArrayDeque<ChatMessage>>(ArrayDeque())
    val messages: StateFlow<ArrayDeque<ChatMessage>> = _messages.asStateFlow()

    private val _isPageLoading = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val isPageLoading: StateFlow<UiState<Boolean>> = _isPageLoading.asStateFlow()

    private val _saveImageState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val saveImageState: StateFlow<UiState<Boolean>> = _saveImageState.asStateFlow()

    private val _isMentorState = MutableStateFlow(false)
    val isMentorState: StateFlow<Boolean> = _isMentorState.asStateFlow()

    private val stompClient = StompManager.stompClient
    private var subscription: Disposable? = null

    fun setRoomId(roomId: Long) {
        this.roomId = roomId
    }

    fun subscribeChatRoom(roomId: Long) {
        handleWebSocketOpened(roomId)
    }

    /**
     * 소켓이 연결되었을 경우 서버에서 가져온 방 Id를 구독하여 메세지 수신 대기하는 함수
     */
    @SuppressLint("CheckResult")
    private fun handleWebSocketOpened(roomId: Long) {
        // 중복 구독 방지
        if (subscription != null) return
        // 채팅방 구독
        val headers = listOf(
            StompHeader("chatRoomId", roomId.toString()),
            StompHeader("senderId", member.id.toString())
        )
        subscription = stompClient?.topic("/sub/chats/$roomId", headers)?.subscribe { message ->
            handleMessage(message)
        }
    }

    private fun handleWebSocketError(exception: Exception? = null) {
        _connectState.update { UiState.Error(exception ?: Exception("WebSocket Error")) }
    }

    private fun handleMessage(message: StompMessage) {
        viewModelScope.launch(Dispatchers.Main) {
            val messageObject = GsonClient.instance.fromJson(message.payload, MessageDto::class.java)
            val chatMessage = getSenderOrReceiver(messageObject)
            Log.d(TAG, "handleMessage: ${chatMessage}")
            val updatedMessages = ArrayDeque(_messages.value)
            updatedMessages.addLast(chatMessage)
            _messages.value = updatedMessages
        }
    }

    fun sendMessage(message: String, roomId: Long) {
        val jsonObject = JSONObject().apply {
            put("content", message)
            put("senderId", member.id)
            put("chatRoomId", roomId)
            put("senderName", member.name)
        }
        val body = jsonObject.toString()

        Log.d(TAG, "sendMessage: $body")
        viewModelScope.launch (Dispatchers.IO) {
            stompClient!!.send("/pub/hello", body).subscribe()
        }
    }

    fun sendFile(file: MultipartBody.Part) {
        viewModelScope.launch {
            chatRepository.sendFile(file, roomId).collect {
                it.handleResponse(
                    onSuccess = {},
                    onError = {
                        Log.d(TAG, "sendFile: ${it.message} ${it.exception?.message}  $file")
                    }
                )
            }
        }
    }

    fun getChatRoomState(roomId: Long) {
        viewModelScope.launch {
            chatRepository.getChatRoom(roomId).collect { result ->
                result.handleResponse(
                    onSuccess = { data ->
                        _chatRoomState.update { UiState.Success(data.data) }
                        if (data.data.mentorId == member.id) _isMentorState.update { true }
                        getMessageStream(roomId)
                        subscribeChatRoom(roomId)
                    },
                    onError = { error ->
                        _chatRoomState.update { UiState.Error(error.exception) }
                    }
                )
            }
        }
    }

    fun getMessageStream(roomId: Long) {
        _isPageLoading.value = UiState.Loading
        viewModelScope.launch {
            chatRepository.getMessages(roomId, currentPage, 20).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { response ->
                        _isPageLoading.value = UiState.Success(true)
                        if (response.data.last) {
                            _hasNextPage.value = false
                        }
                        val updatedMessages = ArrayDeque(_messages.value)
                        response.data.content.forEach {
                            updatedMessages.addFirst(getSenderOrReceiver(it))
                        }
                        Log.d(TAG, "getMessageStream: $updatedMessages")
                        //TODO forEach 이외 방법
                        _messages.value = updatedMessages
                        currentPage++
                    },
                    onError = { error ->
                        _isPageLoading.value = UiState.Error(error.exception)
                        Log.d(TAG, "getMessagesError: $error")
                    }
                )
            }
        }
    }

    // 메세지 보낸 멤버가 로그인 한 멤버인지 다른 멤버인지 구분하는 함수
    private fun getSenderOrReceiver(chatMessage: MessageDto): ChatMessage {
        return when (chatMessage.senderId) {
            member.id -> chatMessage.toSender()
            else -> chatMessage.toReceiver()
        }
    }

    fun saveImageToGallery(imageUrl: String) {
        viewModelScope.launch {
            _saveImageState.update { UiState.Loading }
            val result = fileManager.saveImageToGallery(imageUrl)
            when (result) {
                is SaveFileResult.Success -> _saveImageState.update { UiState.Success(true) }
                is SaveFileResult.Failure -> _saveImageState.update { UiState.Error(result.error) }
            }
        }
    }

    fun downloadFile(fileUrl: String) {
        viewModelScope.launch {
            fileManager.downloadFile(fileUrl)
        }
    }

    fun requestExtendMentoringTime() {
        viewModelScope.launch {
            chatRepository.extendMentoringTime(roomId).collect {
                it.handleResponse(
                    onSuccess = {
                        Log.d(TAG, "extendMentoringTime: success")
                    },
                    onError = {
                        Log.d(TAG, "extendMentoringTime: ${it.exception!!.message}")
                    }
                )
            }
        }
    }

    fun acceptExtendMentoringTime(chatId: Long) {
        viewModelScope.launch {
            chatRepository.processExtendMentoringTime(
                chatId,
                MentoringExtendStatus.ACCEPT
            ).collect {

            }

        }
    }

    fun rejectExtendMentoringTime(chatId: Long) {
        viewModelScope.launch {
            chatRepository.processExtendMentoringTime(
                chatId,
                MentoringExtendStatus.DECLINE
            ).collect {

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
    }
}
