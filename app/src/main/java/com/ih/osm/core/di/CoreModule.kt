package com.ih.osm.core.di

import android.content.Context
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.core.preferences.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManager = NotificationManager(context)

    @Provides
    @Singleton
    fun provideFileHelper(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences,
    ): FileHelper = FileHelper(context, sharedPreferences)
}
