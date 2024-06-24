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
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.data.model.ChatMessageType
import com.minhoi.memento.data.network.SaveFileResult
import com.minhoi.memento.data.network.socket.StompManager
import com.minhoi.memento.repository.chat.ChatRepository
import com.minhoi.memento.ui.UiState
import com.minhoi.memento.utils.FileManager
import com.minhoi.memento.utils.parseLocalDateTime
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

    private val stompClient = StompManager.stompClient
    private var subscription: Disposable? = null

    fun subscribeChatRoom(roomId: Long) {
        handleWebSocketOpened(roomId)
    }

    /**
     * 소켓이 연결되었을 경우 서버에서 가져온 방 Id를 구독하여 메세지 수신 대기하는 함수
     */
    @SuppressLint("CheckResult")
    private fun handleWebSocketOpened(roomId: Long) {
        _connectState.update { UiState.Success(true) }
        // 채팅방 구독
        val headers = listOf(
            StompHeader("chatRoomId", roomId.toString()),
            StompHeader("senderId", member.id.toString())
        )
        subscription = stompClient!!.topic("/sub/chats/$roomId", headers).subscribe { message ->
            handleMessage(message)
        }
    }

    private fun handleWebSocketError(exception: Exception? = null) {
        _connectState.update { UiState.Error(exception ?: Exception("WebSocket Error")) }
    }

    private fun handleMessage(message: StompMessage) {
        viewModelScope.launch(Dispatchers.Main) {
            val json = JSONObject(message.payload)
            Log.d(TAG, "handleMessage: $json")
            val fileType = ChatMessageType.toMessageType(json.getString("fileType"))
            val fileURL = json.getString("fileURL")
            val senderId = json.getLong("senderId")
            val senderName = json.getString("senderName")
            val content: String? = json.getString("content")
            val chatRoomId = json.getLong("chatRoomId")
            val date = json.getString("localDateTime")
            val readCount = json.getInt("readCount")
            val messageObject =
                MessageDto(fileType, fileURL, senderId, chatRoomId, content, date, senderName, readCount)

            val chatMessage = if (senderId == member.id) {
                messageObject.toSender()
            } else {
                messageObject.toReceiver()
            }

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
        if (roomId == -1L)
            return
        viewModelScope.launch {
            chatRepository.sendFile(file, roomId).collectLatest {
                it.handleResponse(
                    onSuccess = {
                        Log.d(TAG, "sendFile: $it   $file")
                        // 라시이클러뷰에 추가
                        //
                    },
                    onError = {
                        Log.d(TAG, "sendFile: ${it.message} ${it.exception?.message}  $file")
                    }
                )
            }
        }
    }

    fun getChatRoomId(receiverId: Long) {
        viewModelScope.launch {
            chatRepository.getRoomId(receiverId).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { data ->
                        roomId = data.data.id
                        _chatRoomState.update { UiState.Success(data.data.id) }
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
                Log.d(TAG, "getMessagesPage: $currentPage")
                result.handleResponse(
                    onSuccess = { response ->
                        _isPageLoading.value = UiState.Success(true)
                        Log.d(TAG, "getMessages: ${messages.value}")
                        if (response.data.last) {
                            _hasNextPage.value = false
                        }
                        val updatedMessages = ArrayDeque(_messages.value)
                        response.data.content.forEach {
                            updatedMessages.addFirst(getSenderOrReceiver(it))
                        }
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
            member.id -> {
                Sender(chatMessage.senderName, chatMessage.content, parseLocalDateTime(chatMessage.date), chatMessage.fileType, chatMessage.fileURL, chatMessage.senderId)
            }
            else -> {
                Receiver(chatMessage.senderName, chatMessage.content, parseLocalDateTime(chatMessage.date), chatMessage.fileType, chatMessage.fileURL, chatMessage.senderId)
            }
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

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
    }
}
