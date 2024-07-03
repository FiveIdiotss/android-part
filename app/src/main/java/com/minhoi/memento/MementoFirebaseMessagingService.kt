package com.minhoi.memento

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.minhoi.memento.data.model.FCMNotification
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

            try {
                Log.d(TAG, "onMessageReceived: ${message.data} ")
                val type = message.data["type"]?.toNotificationType()
                    ?: throw IllegalArgumentException("Invalid notification type")

                val notification = FCMNotification(
                    message.data["senderId"]?.toLongOrNull()
                        ?: throw IllegalArgumentException("Invalid senderId"),
                    message.data["otherPK"]?.toLongOrNull()
                        ?: throw IllegalArgumentException("Invalid otherPK"),
                    message.data["senderName"]
                        ?: throw IllegalArgumentException("Invalid senderName"),
                    message.data["content"]
                        ?: throw IllegalArgumentException("Invalid content"),
                    message.data["senderImageUrl"]
                        ?: throw IllegalArgumentException("Invalid senderImageUrl")
                )

                sendNotification(notification, type)

            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Invalid notification data: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "${e.printStackTrace()}")
            }
        }
    }


    private fun sendNotification(notification: FCMNotification, type: NotificationType) {
        val permissionAllowed = when (type) {
            NotificationType.CHAT -> MentoApplication.notificationPermissionPrefs.getChatPermission()
            NotificationType.REPLY_QUEST -> MentoApplication.notificationPermissionPrefs.getReplyPermission()
            NotificationType.APPLY -> MentoApplication.notificationPermissionPrefs.getApplyPermission()
            NotificationType.MATCHING_COMPLETE, NotificationType.MATCHING_DECLINE -> MentoApplication.notificationPermissionPrefs.getMatchPermission()
        }

        if (!permissionAllowed) return

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("type", type.name)
            putExtra("otherPK", notification.otherPK)
            putExtra("senderName", notification.senderName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notification.senderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = when (type) {
            NotificationType.CHAT -> getString(R.string.channel_chat_id)
            NotificationType.REPLY_QUEST -> getString(R.string.channel_reply_id)
            NotificationType.APPLY -> getString(R.string.channel_apply_id)
            NotificationType.MATCHING_COMPLETE, NotificationType.MATCHING_DECLINE -> getString(R.string.channel_matching_id)
        }

        val title = when (type) {
            NotificationType.CHAT -> notification.senderName
            NotificationType.REPLY_QUEST -> "${notification.senderName} 님이 답글을 작성하였습니다."
            NotificationType.APPLY -> "${notification.senderName} 님이 멘토링을 신청하였습니다."
            NotificationType.MATCHING_COMPLETE -> "${notification.senderName} 님과 멘토링이 성사되었습니다."
            NotificationType.MATCHING_DECLINE -> "${notification.senderName} 님과 멘토링이 성사되지 않았습니다. 멘토링 지원 현황을 확인해주세요."
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.round_corner_blue_filled)
            setContentTitle(title)
            setContentText(notification.content)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setContentIntent(pendingIntent)
            setAutoCancel(true)

            if (type == NotificationType.CHAT) {
                val person = Person.Builder().apply {
                    setName(notification.senderName)
                    convertUriToIconCompat(
                        notification.profileUri,
                        this@MementoFirebaseMessagingService
                    ) { icon ->
                        icon?.let { setIcon(it) }
                    }
                }.build()

                setStyle(
                    NotificationCompat.MessagingStyle(person)
                        .addMessage(notification.content, System.currentTimeMillis(), person)
                )
                setGroup(CHAT_GROUP)
            }
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())

        if (type == NotificationType.CHAT) {
            val summaryBuilder =
                NotificationCompat.Builder(this, getString(R.string.channel_chat_id)).apply {
                    setSmallIcon(R.drawable.chat)
                    setAutoCancel(true)
                    setOnlyAlertOnce(true)
                    setGroup(CHAT_GROUP)
                    setGroupSummary(true)
                }
            notificationManager.notify(0, summaryBuilder.build())
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

    private fun convertUriToIconCompat(
        uri: String,
        context: Context,
        callback: (iconCompat: IconCompat?) -> Unit,
    ) {
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

    enum class NotificationType {
        CHAT, REPLY_QUEST, APPLY, MATCHING_COMPLETE, MATCHING_DECLINE;
    }

    companion object {
        private const val TAG = "FCM"
        private const val CHAT_GROUP = "chat_group"
        private fun String.toNotificationType(): NotificationType {
            return when (this) {
                "CHAT" -> NotificationType.CHAT
                "REPLY_QUEST" -> NotificationType.REPLY_QUEST
                "APPLY" -> NotificationType.APPLY
                "MATCHING_COMPLETE" -> NotificationType.MATCHING_COMPLETE
                "MATCHING_DECLINE" -> NotificationType.MATCHING_DECLINE
                else -> throw IllegalArgumentException("Invalid notification type")
            }
        }
    }
}