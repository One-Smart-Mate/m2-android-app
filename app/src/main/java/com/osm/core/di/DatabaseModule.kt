package com.osm.core.di

import android.content.Context
import androidx.room.Room
import com.osm.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val APP_DATABASE_NAME = "osm_database"

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context)
    = Room.databaseBuilder(context, com.osm.data.database.AppDatabase::class.java, APP_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun providesUserDao(database: com.osm.data.database.AppDatabase) = database.getUserDao()

    @Singleton
    @Provides
    fun providesCardDao(database: com.osm.data.database.AppDatabase) = database.getCardDao()

    @Singleton
    @Provides
    fun providesCardTypeDao(database: com.osm.data.database.AppDatabase) = database.getCardTypeDao()

    @Singleton
    @Provides
    fun providesPreclassifiereDao(database: com.osm.data.database.AppDatabase) = database.getPreclassifierDao()

    @Singleton
    @Provides
    fun providesPriorityDao(database: com.osm.data.database.AppDatabase) = database.getPriorityDao()

    @Singleton
    @Provides
    fun providesLevelDao(database: com.osm.data.database.AppDatabase) = database.getLevelDao()

    @Singleton
    @Provides
    fun providesEvidenceDao(database: com.osm.data.database.AppDatabase) = database.getEvidenceDao()

    @Singleton
    @Provides
    fun providesEmployeeDao(database: com.osm.data.database.AppDatabase) = database.getEmployeeDao()
}