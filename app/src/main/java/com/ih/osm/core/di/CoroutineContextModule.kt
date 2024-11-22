package com.ih.osm.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object CoroutineContextModule {
    @Provides
    @Singleton
    fun providesCoroutineContext(): CoroutineContext = Dispatchers.IO
}
