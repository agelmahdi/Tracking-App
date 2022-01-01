package com.agelmahdi.trackingapp.DI

import android.content.Context
import androidx.room.Room
import com.agelmahdi.trackingapp.DB.RunDAO
import com.agelmahdi.trackingapp.DB.RunningDatabase
import com.agelmahdi.trackingapp.Others.Constants.RUNNING_DB_NAME
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

}