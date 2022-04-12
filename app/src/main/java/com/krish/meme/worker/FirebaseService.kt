package com.krish.meme.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.load.engine.Resource
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.krish.meme.Constants.Companion.CHANNEL_ID
import com.krish.meme.MainActivity
import com.krish.meme.R
import java.io.IOException
import java.net.URL
import kotlin.random.Random
private const val TAG = "FirebaseService"

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived: ${message.data}")
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()
        var bitmap : Bitmap?= null
        try {
            val inputStream = URL(message.data["imageUrl"]).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
            Log.d(TAG, "Bitmap: $bitmap")
        }catch (e : IOException){
            Log.d(TAG, "Error : ${e.message}")
        }
        createNotificationChannel(notificationManager)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .setBigContentTitle(message.data["title"])
            .setSummaryText(message.data["message"])

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setStyle(bigPictureStyle)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }
        
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}