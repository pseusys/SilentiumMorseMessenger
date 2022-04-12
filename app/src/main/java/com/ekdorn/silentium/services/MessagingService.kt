package com.ekdorn.silentium.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Base64
import androidx.core.app.NotificationCompat
import com.ekdorn.silentium.R
import com.ekdorn.silentium.activities.SilentActivity
import com.ekdorn.silentium.core.toReadableString
import com.ekdorn.silentium.managers.DatabaseManager
import com.ekdorn.silentium.managers.NetworkManager
import com.ekdorn.silentium.managers.UserManager
import com.ekdorn.silentium.models.Message
import com.ekdorn.silentium.models.Payload
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.util.*


class MessagingService : FirebaseMessagingService() {
    companion object {
        var lateToken: String? = null
    }

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onNewToken(token: String) = ioScope.launch {
        if (Firebase.auth.currentUser != null) NetworkManager.updateUser(token = token)
        else lateToken = token
    }.ensureActive()

    @Suppress("UnspecifiedImmutableFlag")
    @OptIn(ExperimentalSerializationApi::class)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.body?.let {
            val payload = ProtoBuf.decodeFromByteArray<Payload>(Base64.decode(it, Base64.DEFAULT))
            val from = message.data["fromId"]!!

            if (from != UserManager[this].value!!.id) ioScope.launch {
                DatabaseManager[this@MessagingService].messageDAO().add(Message(payload, Date(), false, from, from))
            }

            val intent = Intent(this, SilentActivity::class.java).putExtra(SilentActivity.NAVIGATE_TO_MESSAGES, from)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val channelId = getString(R.string.notification_channel_id)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_logo)
                .setContentTitle(message.notification?.title)
                .setContentText(payload.data.toReadableString(this))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(message.data["customId"]?.toInt() ?: 2, notificationBuilder.build())
        }
    }
}
