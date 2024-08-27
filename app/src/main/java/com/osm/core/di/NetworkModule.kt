package com.osm.core.di

import com.google.gson.GsonBuilder
import com.osm.data.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl(): String = "https://service-m2-development.up.railway.app/"

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder().create()
        ))
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): com.osm.data.api.ApiService = retrofit.create(com.osm.data.api.ApiService::class.java)

}
