package com.ih.m2.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object CoroutineContextModule {

    @Provides
    @Singleton
    fun providesCoroutineContext(): CoroutineContext = Dispatchers.IO
}