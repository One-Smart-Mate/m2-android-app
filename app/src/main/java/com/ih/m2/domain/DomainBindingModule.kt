package com.ih.m2.domain

import com.ih.m2.domain.usecase.card.GetCardDetailUseCase
import com.ih.m2.domain.usecase.card.GetCardDetailUseCaseImpl
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.card.GetCardsUseCaseImpl
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCaseImpl
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCaseImpl
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.domain.usecase.user.GetUserUseCaseImpl
import com.ih.m2.domain.usecase.login.LoginUseCase
import com.ih.m2.domain.usecase.login.LoginUseCaseImpl
import com.ih.m2.domain.usecase.logout.LogoutUseCase
import com.ih.m2.domain.usecase.logout.LogoutUseCaseImpl
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCaseImpl
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCase
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCaseImpl
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

    @Binds
    fun bindGetUserUseCase(getUserUseCaseImpl: GetUserUseCaseImpl): GetUserUseCase

    @Binds
    fun bindLogoutUseCase(logoutUseCaseImpl: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    fun bindGetCardsUseCase(getCardsUseCaseImpl: GetCardsUseCaseImpl): GetCardsUseCase

    @Binds
    fun bindGetCardTypeUseCase(getCardTypesUseCaseImpl: GetCardTypesUseCaseImpl): GetCardTypesUseCase

    @Binds
    fun bindGetPreclassifiersUseCase(getPreclassifiersUseCaseImpl: GetPreclassifiersUseCaseImpl): GetPreclassifiersUseCase

    @Binds
    fun bindGetPrioritiesUseCase(getPrioritiesUseCaseImpl: GetPrioritiesUseCaseImpl): GetPrioritiesUseCase

    @Binds
    fun bindSyncCatalogUseCase(syncCatalogsUseCaseImpl: SyncCatalogsUseCaseImpl): SyncCatalogsUseCase

    @Binds
    fun bindGetCardDetailUseCase(getCardDetailUseCaseImpl: GetCardDetailUseCaseImpl): GetCardDetailUseCase
}