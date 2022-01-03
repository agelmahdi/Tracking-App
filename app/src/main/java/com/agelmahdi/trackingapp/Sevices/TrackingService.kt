package com.agelmahdi.trackingapp.Sevices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.agelmahdi.trackingapp.Others.Constants.ACTION_PAUSE_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.agelmahdi.trackingapp.Others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_STOP_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.NOTIFICATION_CHANNEL_ID
import com.agelmahdi.trackingapp.Others.Constants.NOTIFICATION_CHANNEL_NAME
import com.agelmahdi.trackingapp.Others.Constants.NOTIFICATION_ID
import com.agelmahdi.trackingapp.R
import com.agelmahdi.trackingapp.UI.MainActivity
import timber.log.Timber


class TrackingService(): BackgroundLocationService() {

    var isServiceStarted = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isServiceStarted) {
                        startForegroundService()
                        isServiceStarted = !isServiceStarted
                    } else {
                        Timber.d("Resuming service....")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d(ACTION_PAUSE_SERVICE)
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d(ACTION_START_OR_RESUME_SERVICE)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {

        addEmptyPolyline()

        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuild = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running app")
            .setContentText("00:00:00")
            .setContentIntent(mainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuild.build())

    }

    private fun mainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }


}