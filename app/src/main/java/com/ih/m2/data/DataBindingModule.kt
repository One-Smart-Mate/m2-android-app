package com.ih.m2.data

import com.ih.m2.data.repository.AuthRepositoryImpl
import com.ih.m2.domain.repository.AuthRepository
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataBindingModule {
    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}