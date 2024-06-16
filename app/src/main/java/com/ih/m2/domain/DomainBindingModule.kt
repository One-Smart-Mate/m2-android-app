package com.ih.m2.domain

import com.ih.m2.domain.usecase.login.LoginUseCase
import com.ih.m2.domain.usecase.login.LoginUseCaseImpl
import com.ih.m2.domain.usecase.saveuser.SaveUserUseCase
import com.ih.m2.domain.usecase.saveuser.SaveUserUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DomainBindingModule {

    @Binds
    fun bindLoginUseCase(loginUseCaseImpl: LoginUseCaseImpl): LoginUseCase

    @Binds
    fun bindSaveUserUseCase(saveUserUseCaseImpl: SaveUserUseCaseImpl): SaveUserUseCase
}