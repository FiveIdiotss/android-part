package com.minhoi.memento.data.network.socket

import android.annotation.SuppressLint
import android.util.Log
import com.minhoi.memento.BuildConfig
import io.reactivex.Flowable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

private const val url = "ws://" + BuildConfig.SERVER_IP + ":8080/ws"
private const val TAG = "StompManager"

object StompManager {

    private const val MAX_RECONNECT_ATTEMPTS = 3
    private const val NETWORK_ERROR_MESSAGE = "네트워크 오류가 발생하였습니다. 인터넷 연결을 확인해 주세요."
    var stompClient: StompClient? = null
        private set

    private var reconnectAttempts = 0
    private var isReconnecting = false

    private val _connectionEvent = MutableSharedFlow<StompConnectionEvent>()
    val connectionEvent: SharedFlow<StompConnectionEvent> = _connectionEvent.asSharedFlow()

    /** 앱 실행 시 헤더 없이 소켓 연결 */
    @SuppressLint("CheckResult")
    fun connectToSocket() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url) ?: null
        stompClient?.let { client ->
            client.connect()
            observeLifecycle(client)
        }
    }

    private fun observeLifecycle(client: StompClient) {
        client.lifecycle().toFlow()
            .onEach { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        reconnectAttempts = 0
                        emitErrorEvent(StompConnectionEvent.Connected)
                        Log.i(TAG, "Connection opened")
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        Log.i(TAG, "Connection closed")
                        reconnect(client,Exception("Connection closed"))
                    }
                    LifecycleEvent.Type.ERROR -> {
                        Log.e(TAG, "Connection error: ${event.exception?.message}")
                        reconnect(client, event.exception)
                    }
                    else -> {
                        Log.i(TAG, "Other event: ${event.message}")
                    }
                }
            }.catch {
                emitErrorEvent(StompConnectionEvent.Error(it, NETWORK_ERROR_MESSAGE))
            }.launchIn(GlobalScope)
    }

    private suspend fun reconnect(
        client: StompClient,
        exception: Throwable
    ) {
        if (isReconnecting) {
            Log.d(TAG, "재연결중")
            return
        }
        isReconnecting = true
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++
            Log.i(TAG, "reconnect ($reconnectAttempts/$MAX_RECONNECT_ATTEMPTS)")
            delay(1000L) // 재연결 딜레이 설정
            client.reconnect()
        } else {
            Log.i(TAG, "재연결 횟수 초과")
            emitErrorEvent(StompConnectionEvent.Error(exception, NETWORK_ERROR_MESSAGE))
        }
        isReconnecting = false
    }

    private fun emitErrorEvent(event: StompConnectionEvent) {
        GlobalScope.launch {
            _connectionEvent.emit(event)
        }
    }

    // Flow 변환을 위한 확장함수
    private fun Flowable<LifecycleEvent>.toFlow(): Flow<LifecycleEvent> = callbackFlow {
        val subscription = this@toFlow.subscribe(
            { event -> trySend(event).isSuccess },
            { throwable -> close(throwable) },
            { close() }
        )
        awaitClose { subscription.dispose() }
    }

    fun disconnect() {
        stompClient?.disconnect()
    }

    sealed class StompConnectionEvent {
        object Connected : StompConnectionEvent()
        data class Error(
            val exception: Throwable,
            val message: String,
        ) : StompConnectionEvent()
    }
}
