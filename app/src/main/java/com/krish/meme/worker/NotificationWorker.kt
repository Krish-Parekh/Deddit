package com.krish.meme.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "NotificationWorker"

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val workerUtils: WorkerUtils
) : Worker(ctx, params) {


    override fun doWork(): Result {
        try {
            workerUtils.getToken()
            Log.d(TAG, "We are inside this")
        } catch (e: Exception) {
            Log.d(TAG, "doWork: ${e.message}")
            return Result.failure()
        }
        return Result.success()
    }
}