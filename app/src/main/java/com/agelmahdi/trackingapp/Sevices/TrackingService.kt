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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.agelmahdi.trackingapp.Others.Constants.ACTION_PAUSE_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.agelmahdi.trackingapp.Others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_STOP_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.NOTIFICATION_CHANNEL_ID
import com.agelmahdi.trackingapp.Others.Constants.NOTIFICATION_CHANNEL_NAME
import com.agelmahdi.trackingapp.Others.Constants.NOTIFICATION_ID
import com.agelmahdi.trackingapp.Others.Constants.TIMER_UPDATE_INTERVAL
import com.agelmahdi.trackingapp.Others.TrackingUtil
import com.agelmahdi.trackingapp.R
import com.agelmahdi.trackingapp.UI.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService() : BackgroundLocationService() {

    var isServiceStarted = true

    private var isTimerEnabled = false

    // time difference between now and startedTime
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecTimestamp = 0L

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder

        isTracking.observe(this, Observer {
            updateNotificationState(it)
        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isServiceStarted) {
                        startForegroundService()
                        isServiceStarted = !isServiceStarted
                    } else {
                        Timber.d("Resuming service....")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d(ACTION_START_OR_RESUME_SERVICE)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {

            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted

                timeRunInMillis.postValue(timeRun + lapTime)

                if (timeRunInMillis.value!! >= lastSecTimestamp + 1000L) {
                    timeRunInSec.postValue(timeRunInSec.value!! + 1)
                    lastSecTimestamp += 1000L
                }

                delay(TIMER_UPDATE_INTERVAL)
            }

            timeRun += lapTime
        }

    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun startForegroundService() {
        startTimer()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSec.observe(this, Observer {
            val notification = curNotificationBuilder
                .setContentText(TrackingUtil.formattedStopWatch(it * 1000L))
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        })
    }

    private fun updateNotificationState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // swap out the action when we click on it
        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        curNotificationBuilder = baseNotificationBuilder
            .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)

        notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())

    }

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