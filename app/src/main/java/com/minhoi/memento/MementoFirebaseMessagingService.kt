package com.minhoi.memento

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.minhoi.memento.data.model.ChatNotification
import com.minhoi.memento.data.model.PostNotification
import com.minhoi.memento.data.network.RetrofitClient
import com.minhoi.memento.data.network.service.NotificationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MementoFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationService =
        RetrofitClient.getLoggedInInstance().create(NotificationService::class.java)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToServer(token)
        Log.d(TAG, "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // 수신한 메세지 처리
        // notification이 존재하면 앱이 백그라운드에 있을 때 onMessageReceived에서 제어 불가. 따라서 message.data에 모든 정보 존재

        super.onMessageReceived(message)

        if (message.data.isNotEmpty()) {
            Log.d(TAG, "onMessageReceived: ${message.data} ")
            val type = message.data["type"]
            when (type) {
                "chat" -> {
                    val notification = ChatNotification(
                        message.data["roomId"]!!.toLong(),
                        message.data["senderName"]!!,
                        message.data["content"]!!,
                        message.data["profileImageUrl"]!!
                    )
                    sendChatNotification(notification)
                }

                "post" -> {
                    val notification = PostNotification(message.data["content"]!!)
                    sendPostNotification(notification)
                }
            }
        }
    }

    private fun saveTokenToServer(token: String) {

        notificationService.saveToken(token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Token successfully saved to server.")
                } else {
                    Log.e(TAG, "Failed to save token to server. Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Error saving token to server: ${t.localizedMessage}")
            }
        })
    }

    private fun sendChatNotification(notification: ChatNotification) {
        val person = Person.Builder().apply {
            setName(notification.senderName)
            convertUriToIconCompat(notification.profileUri, this@MementoFirebaseMessagingService) { icon ->
                icon?.let {
                    setIcon(it)
                }
            }
        }.build()

        // NotificationCompat.MessagingStyle을 사용하여 채팅 스타일의 알림 생성

        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.channel_chat)).apply {
            setSmallIcon(R.drawable.chat)
            setContentTitle("새로운 채팅 메시지")
            setStyle(NotificationCompat.MessagingStyle(person)
                .addMessage(notification.content, System.currentTimeMillis(), person))
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notification.roomId.toInt(), notificationBuilder.build())
    }

    private fun sendPostNotification(notification: PostNotification) {

    }

    private fun convertUriToIconCompat(uri: String, context: Context, callback: (iconCompat: IconCompat?) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .apply(RequestOptions().circleCrop())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val iconCompat = IconCompat.createWithBitmap(resource)
                    callback(iconCompat)
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    // 필요한 경우 여기에 정리 코드를 추가
                }

                override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    callback(null)
                }
            })
    }

    companion object {
        private const val TAG = "FCM"
    }

}