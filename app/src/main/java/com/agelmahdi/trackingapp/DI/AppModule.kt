package com.agelmahdi.trackingapp.DI

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.agelmahdi.trackingapp.DB.RunDAO
import com.agelmahdi.trackingapp.DB.RunningDatabase
import com.agelmahdi.trackingapp.Others.Constants
import com.agelmahdi.trackingapp.Others.Constants.PREFERENCE_FIRST_TIME_LUNCH
import com.agelmahdi.trackingapp.Others.Constants.PREFERENCE_USER_NAME
import com.agelmahdi.trackingapp.Others.Constants.PREFERENCE_USER_WEIGHT
import com.agelmahdi.trackingapp.Others.Constants.RUNNING_DB_NAME
import com.agelmahdi.trackingapp.Others.Constants.SHARED_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DB_NAME
    ).build()

    @Provides
    @Singleton
    fun provideRunDao(db: RunningDatabase) = db.getRunDAO()

    @Provides
    @Singleton
    fun provideSharedPrefernces(
        @ApplicationContext app: Context
    ) = app.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideUserName(
        sharedPreferences: SharedPreferences
    ) = sharedPreferences.getString(PREFERENCE_USER_NAME, "") ?: ""

    @Provides
    @Singleton
    fun provideUserWeight(
        sharedPreferences: SharedPreferences
    ) = sharedPreferences.getFloat(PREFERENCE_USER_WEIGHT, 0f)

    @Provides
    @Singleton
    fun provideFirstLunch(
        sharedPreferences: SharedPreferences
    ) = sharedPreferences.getBoolean(PREFERENCE_FIRST_TIME_LUNCH, true)

}