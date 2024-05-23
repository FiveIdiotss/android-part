package com.minhoi.memento.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.minhoi.memento.MentoApplication
import com.minhoi.memento.data.dto.chat.ChatMessage
import com.minhoi.memento.data.dto.chat.MessageDto
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.data.model.ChatFileType
import com.minhoi.memento.repository.chat.ChatRepository
import com.minhoi.memento.ui.UiState
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
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val TAG = ChatViewModel::class.java.simpleName
    private val member = MentoApplication.memberPrefs.getMemberPrefs()
    private var currentPage: Int = 1
    private val _hasNextPage = MutableLiveData<Boolean>(true)
    private var roomId = -1L
    val hasNextPage: LiveData<Boolean> = _hasNextPage

    //    private val _chatRooms: MutableStateFlow<>
    private val _connectState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val connectState: StateFlow<UiState<Boolean>> = _connectState.asStateFlow()

    private val _chatRoomState = MutableStateFlow<UiState<Long>>(UiState.Loading)
    val chatRoomState: StateFlow<UiState<Long>> = _chatRoomState.asStateFlow()

    private val _messages = MutableLiveData<ArrayDeque<ChatMessage>>()
    val messages: LiveData<ArrayDeque<ChatMessage>> = _messages
    private val tempMessages = ArrayDeque<ChatMessage>()

    private val _isPageLoading = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val isPageLoading: StateFlow<UiState<Boolean>> = _isPageLoading.asStateFlow()

    private val intervalMillis = 5000L
    private var stomp: StompClient? = null
    private var stompConnection: Disposable? = null
    private lateinit var topic: Disposable
    private val client = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    fun connectToWebSocket(roomId: Long) {
        val url = "ws://menteetor.site:8080/ws"
        if (stomp != null) {
            return
        }
        stomp = StompClient(client, intervalMillis).apply {
            this@apply.url = url
        }

        viewModelScope.launch {
            try {
                stompConnection = stomp!!.connect().subscribe { event ->
                    when (event.type) {
                        Event.Type.OPENED -> handleWebSocketOpened(roomId)
                        Event.Type.CLOSED -> handleWebSocketClosed()
                        Event.Type.ERROR -> handleWebSocketError()
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                handleWebSocketError(e)
            }
        }
    }

    /**
     * 소켓이 연결되었을 경우 서버에서 가져온 방 Id를 구독하여 메세지 수신 대기하는 함수
     */
    private fun handleWebSocketOpened(roomId: Long) {
        _connectState.update { UiState.Success(true) }
        // 채팅방 구독

        topic = stomp!!.join("/sub/chats/$roomId").subscribe { message ->
            Log.d("Messageqqq", "connectToWebSocket: $message")
            viewModelScope.launch(Dispatchers.Main) {
                handleMessage(message)
            }
        }
    }

    private fun handleWebSocketClosed() {
        // WebSocket이 닫혔을 때의 처리
    }

    private fun handleWebSocketError(exception: Exception? = null) {
        _connectState.update { UiState.Error(exception ?: Exception("WebSocket Error")) }
    }

    private fun handleMessage(message: String) {
        try {
            val json = JSONObject(message)
            Log.d(TAG, "handleMessage: $json")
            val fileType = ChatFileType.toFileType(json.getString("fileType")) ?: return
            val fileURL = json.getString("fileURL")
            val senderId = json.getLong("senderId")
            val senderName = json.getString("senderName")
            val content: String? = json.getString("content")
            val chatRoomId = json.getLong("chatRoomId")
            val date = json.getString("localDateTime")
            val messageObject =
                MessageDto(fileType, fileURL, senderId, chatRoomId, content, date, senderName)
            tempMessages.addLast(getSenderOrReceiver(messageObject))
            _messages.value = tempMessages

        } catch (e: Exception) {
            Log.d(TAG, "handleMessage: ${e.printStackTrace()}")
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
            stomp!!.send("/pub/hello", body).subscribe {
                Log.d(TAG, "sendMessage: $it")
            }
        }
    }

    fun sendImage(image: String, roomId: Long) {
        val jsonObject = JSONObject().apply {
            put("content", null)
            put("senderId", member.id)
            put("chatRoomId", roomId)
            put("senderName", member.name)
            put("image", image)
        }
        val body = jsonObject.toString()
        viewModelScope.launch (Dispatchers.IO) {
            stomp!!.send("/pub/hello", body).subscribe {
                Log.d(TAG, "sendMessage: $it")
            }
        }
    }

    fun disconnect() {
        stompConnection?.dispose()
    }

    fun getChatRoomId(receiverId: Long) {
        viewModelScope.launch {
            chatRepository.getRoomId(receiverId).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { room ->
                        roomId = room.id
                        _chatRoomState.update { UiState.Success(room.id) }
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
                    onSuccess = { messages ->
                        _isPageLoading.value = UiState.Success(true)
                        Log.d(TAG, "getMessages: $messages")
                        if (messages.last) {
                            _hasNextPage.value = false
                        }
                        messages.content.forEach {
                            tempMessages.addFirst(getSenderOrReceiver(it))
                        }
                        _messages.value = tempMessages
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

}