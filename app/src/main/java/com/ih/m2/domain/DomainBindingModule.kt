package com.ih.m2.domain

import com.ih.m2.domain.usecase.LoginUseCase
import com.ih.m2.domain.usecase.LoginUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DomainBindingModule {

    @Binds
    fun bindLoginUseCase(loginUseCaseImpl: LoginUseCaseImpl): LoginUseCase
}