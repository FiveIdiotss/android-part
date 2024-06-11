package com.minhoi.memento.data.network.socket

import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

private const val url = "ws://menteetor.site:8080/ws"

object StompManager {

    var stompClient: StompClient? = null
        private set

    /** 앱 실행시 해더 넣지 않고 소켓 연결 */
    fun connectToSocket() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        stompClient?.connect()
    }

    fun disconnect() {
        stompClient?.disconnect()
    }
}