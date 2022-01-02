package com.agelmahdi.trackingapp.Sevices

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.agelmahdi.trackingapp.Others.Constants.ACTION_PAUSE_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.agelmahdi.trackingapp.Others.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

class TrackingService: LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d(ACTION_START_OR_RESUME_SERVICE)
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
}