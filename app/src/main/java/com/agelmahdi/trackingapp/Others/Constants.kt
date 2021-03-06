package com.agelmahdi.trackingapp.Others

import android.graphics.Color

object Constants {

    const val RUNNING_DB_NAME = "running_db"
    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val SHARED_PREFERENCES = "shardPref"
    const val PREFERENCE_FIRST_TIME_LUNCH = "PREFERENCE_FIRST_TIME_LUNCH"
    const val PREFERENCE_USER_NAME = "REFERENCE_USER_NAME"
    const val PREFERENCE_USER_WEIGHT = "PREFERENCE_USER_WEIGHT"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val TIMER_UPDATE_INTERVAL = 50L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WITH = 8f
    const val CAMERA_ZOOM = 15f


}