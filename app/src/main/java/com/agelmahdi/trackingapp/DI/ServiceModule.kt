package com.agelmahdi.trackingapp.DI

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.agelmahdi.trackingapp.Others.Constants
import com.agelmahdi.trackingapp.Others.Constants.NOTIFICATION_CHANNEL_ID
import com.agelmahdi.trackingapp.R
import com.agelmahdi.trackingapp.UI.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context,
    ) = FusedLocationProviderClient(context)

    @Provides
    @ServiceScoped
    fun provideMainActivityPendingIntent(
        @ApplicationContext context: Context,
    ) = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @Provides
    @ServiceScoped
    fun provideBaseNotificationBuild(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Running app")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

    @Provides
    @ServiceScoped
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

}