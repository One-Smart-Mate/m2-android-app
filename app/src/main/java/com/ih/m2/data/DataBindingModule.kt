package com.ih.m2.data

import com.ih.m2.data.repository.auth.AuthRepositoryImpl
import com.ih.m2.data.repository.local.LocalRepositoryImpl
import com.ih.m2.domain.repository.auth.AuthRepository
import com.ih.m2.domain.repository.local.LocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataBindingModule {
    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindLocalRepository(localRepositoryImpl: LocalRepositoryImpl): LocalRepository
}