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

    fun connectToWebSocket(receiverId: Long) {
        val url = "ws://menteetor.site:8080/ws"
        stomp = StompClient(client, intervalMillis).apply {
            this@apply.url = url
        }

        viewModelScope.launch {
            try {
                stompConnection = stomp.connect().subscribe { event ->
                    when (event.type) {
                        Event.Type.OPENED -> handleWebSocketOpened()
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

    private fun handleWebSocketOpened() {
        _connectState.update { UiState.Success(true) }
        // 채팅방 구독

        topic = stomp.join("/sub/chats/52").subscribe { message ->
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

    private fun handleMessage(message: Any) {
        if (message is String) {
            temp.add(Receiver("7:58", message, 1L))
            _messages.value = temp
        }
    }

    fun sendMessage(message: String) {
        val jsonObject = JSONObject()
        jsonObject.put("content", message)
        jsonObject.put("receiverId", 1)
        val body = jsonObject.toString()
        viewModelScope.launch {
            stomp.send("/pub/hello/52", body).subscribe {
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