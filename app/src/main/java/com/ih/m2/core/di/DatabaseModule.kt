package com.ih.m2.core.di

import android.content.Context
import androidx.room.Room
import com.ih.m2.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val APP_DATABASE_NAME = "m2_database"

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context)
    = Room.databaseBuilder(context, AppDatabase::class.java, APP_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun providesUserDao(database: AppDatabase) = database.getUserDao()
}