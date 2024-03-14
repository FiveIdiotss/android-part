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
import com.minhoi.memento.data.dto.chat.Receiver
import com.minhoi.memento.data.dto.chat.Sender
import com.minhoi.memento.repository.ChatRepository
import com.minhoi.memento.ui.UiState
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatViewModel : ViewModel() {
    private val TAG = ChatViewModel::class.java.simpleName
    private val chatRepository = ChatRepository()
    private val member = MentoApplication.memberPrefs.getMemberPrefs()

    //    private val _chatRooms: MutableStateFlow<>
    private val _connectState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val connectState: StateFlow<UiState<Boolean>> = _connectState.asStateFlow()

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val temp = mutableListOf<ChatMessage>()

    private val client = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    private val intervalMillis = 5000L
    private lateinit var stomp: StompClient
    private lateinit var stompConnection: Disposable
    private lateinit var topic: Disposable
    private val _chatRoomState = MutableStateFlow<UiState<Long>>(UiState.Empty)
    val chatRoomState: StateFlow<UiState<Long>> = _chatRoomState.asStateFlow()

    fun connectToWebSocket(roomId: Long) {
        val url = "ws://menteetor.site:8080/ws"
        stomp = StompClient(client, intervalMillis).apply {
            this@apply.url = url
        }

        viewModelScope.launch {
            try {
                stompConnection = stomp.connect().subscribe { event ->
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

        topic = stomp.join("/sub/chats/$roomId").subscribe { message ->
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

            val senderId = json.getString("senderId")
            val senderName = json.getString("sender")
            val content = json.getString("content")

            when (senderId) {
                member.id.toString() -> {
                    temp.add(Sender(senderId.toLong(), senderName, content, "7:58"))
                }
                else -> {
                    temp.add(Receiver(senderId.toLong(), senderName, content, "7:58"))
                }
            }
            _messages.value = temp
        } catch (e: Exception) {
            Log.d(TAG, "handleMessage: ${e.printStackTrace()}")
        }
    }

    fun sendMessage(message: String) {
        val jsonObject = JSONObject().apply {
            put("content", message)
            put("senderId", member.id)
            put("senderName", member.name)
        }

        val body = jsonObject.toString()
        Log.d(TAG, "sendMessage: $body")
        viewModelScope.launch (Dispatchers.IO) {
            stomp.send("/pub/hello", body).subscribe {
                Log.d(TAG, "sendMessage: $it")
            }
        }
    }

    fun disconnect() {
        stompConnection.dispose()
    }

    fun getChatRoomId(receiverId: Long) {
        viewModelScope.launch {
            chatRepository.getRoomId(receiverId).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { room ->
                        _chatRoomState.update { UiState.Success(room.id) }
                    },
                    onError = { error ->
                        _chatRoomState.update { UiState.Error(Throwable(error)) }
                    }
                )
            }
        }
    }

    fun getChatRooms() {
        viewModelScope.launch {
            chatRepository.getChatRooms(2L).collectLatest { result ->
                result.handleResponse(
                    onSuccess = { chatRooms ->
                        Log.d(TAG, "getChatRooms: $chatRooms")
                    },
                    onError = { error ->
                        // Handle error
                    }
                )
            }
        }
    }
}