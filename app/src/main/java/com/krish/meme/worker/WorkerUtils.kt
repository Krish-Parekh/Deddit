package com.krish.meme.worker

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.krish.meme.di.MemeRetro
import com.krish.meme.di.Notification
import com.krish.meme.model.NotificationData
import com.krish.meme.model.PushNotification
import com.krish.meme.network.NotificationService
import com.krish.meme.network.PostService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WorkerUtils"


class WorkerUtils @Inject constructor(@Notification private val notificationService: NotificationService,@MemeRetro private val postService: PostService){

    fun getToken() {
        Log.d(TAG, "Get Token Called")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCW registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            if (task.isComplete) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = postService.getAllPost("meme", 1)
                        response.memes[0].apply {
                            sentNotification(PushNotification(NotificationData(this.subreddit,this.title,this.url),token))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error : ${e.message}")
                    }
                }
            }
        }
    }

    private fun sentNotification(notification: PushNotification) {
        Log.d(TAG, "sentNotification: ")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = notificationService.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d(TAG, "Response : ${response.body()}")
                } else {
                    Log.d(TAG, "Not Success : ${response.errorBody().toString()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error : ${e.message}")
            }
        }
    }
}
