package com.ih.m2.domain

import com.ih.m2.domain.usecase.card.GetCardDetailUseCase
import com.ih.m2.domain.usecase.card.GetCardDetailUseCaseImpl
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.card.GetCardsUseCaseImpl
import com.ih.m2.domain.usecase.card.GetCardsZoneUseCase
import com.ih.m2.domain.usecase.card.GetCardsZoneUseCaseImpl
import com.ih.m2.domain.usecase.card.SaveCardSolutionUseCase
import com.ih.m2.domain.usecase.card.SaveCardSolutionUseCaseImpl
import com.ih.m2.domain.usecase.card.SaveCardUseCase
import com.ih.m2.domain.usecase.card.SaveCardUseCaseImpl
import com.ih.m2.domain.usecase.card.SyncCardUseCase
import com.ih.m2.domain.usecase.card.SyncCardUseCaseImpl
import com.ih.m2.domain.usecase.card.SyncCardsUseCase
import com.ih.m2.domain.usecase.card.SyncCardsUseCaseImpl
import com.ih.m2.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypeUseCaseImpl
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCaseImpl
import com.ih.m2.domain.usecase.catalogs.CleanCatalogsUseCase
import com.ih.m2.domain.usecase.catalogs.CleanCatalogsUseCaseImpl
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.m2.domain.usecase.catalogs.SyncCatalogsUseCaseImpl
import com.ih.m2.domain.usecase.employee.GetEmployeesUseCase
import com.ih.m2.domain.usecase.employee.GetEmployeesUseCaseImpl
import com.ih.m2.domain.usecase.firebase.GetFirebaseTokenUseCase
import com.ih.m2.domain.usecase.firebase.GetFirebaseTokenUseCaseImpl
import com.ih.m2.domain.usecase.firebase.SyncFirebaseTokenUseCase
import com.ih.m2.domain.usecase.firebase.SyncFirebaseTokenUseCaseImpl
import com.ih.m2.domain.usecase.level.GetLevelsUseCase
import com.ih.m2.domain.usecase.level.GetLevelsUseCaseImpl
import com.ih.m2.domain.usecase.login.LoginUseCase
import com.ih.m2.domain.usecase.login.LoginUseCaseImpl
import com.ih.m2.domain.usecase.logout.LogoutUseCase
import com.ih.m2.domain.usecase.logout.LogoutUseCaseImpl
import com.ih.m2.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.ih.m2.domain.usecase.notifications.GetFirebaseNotificationUseCaseImpl
import com.ih.m2.domain.usecase.password.ResetPasswordUseCase
import com.ih.m2.domain.usecase.password.ResetPasswordUseCaseImpl
import com.ih.m2.domain.usecase.password.SendRestorePasswordCodeUseCase
import com.ih.m2.domain.usecase.password.SendRestorePasswordCodeUseCaseImpl
import com.ih.m2.domain.usecase.password.VerifyPasswordCodeUseCase
import com.ih.m2.domain.usecase.password.VerifyPasswordCodeUseCaseImpl
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCaseImpl
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCase
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCaseImpl
import com.ih.m2.domain.usecase.saveuser.SaveUserUseCase
import com.ih.m2.domain.usecase.saveuser.SaveUserUseCaseImpl
import com.ih.m2.domain.usecase.user.GetUserUseCase
import com.ih.m2.domain.usecase.user.GetUserUseCaseImpl
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
    fun bindGetCardTypesUseCase(getCardTypesUseCaseImpl: GetCardTypesUseCaseImpl): GetCardTypesUseCase

    @Binds
    fun bindGetPreclassifiersUseCase(getPreclassifiersUseCaseImpl: GetPreclassifiersUseCaseImpl): GetPreclassifiersUseCase

    @Binds
    fun bindGetPrioritiesUseCase(getPrioritiesUseCaseImpl: GetPrioritiesUseCaseImpl): GetPrioritiesUseCase

    @Binds
    fun bindSyncCatalogUseCase(syncCatalogsUseCaseImpl: SyncCatalogsUseCaseImpl): SyncCatalogsUseCase

    @Binds
    fun bindGetCardDetailUseCase(getCardDetailUseCaseImpl: GetCardDetailUseCaseImpl): GetCardDetailUseCase

    @Binds
    fun bindCleanCatalogsUseCase(cleanCatalogsUseCaseImpl: CleanCatalogsUseCaseImpl): CleanCatalogsUseCase

    @Binds
    fun bindGetCardsZoneUseCase(getCardsZoneUseCaseImpl: GetCardsZoneUseCaseImpl): GetCardsZoneUseCase

    @Binds
    fun bindGetLevelsUseCase(getLevelsUseCaseImpl: GetLevelsUseCaseImpl): GetLevelsUseCase

    @Binds
    fun bindSaveCardUseCase(saveCardUseCaseImpl: SaveCardUseCaseImpl): SaveCardUseCase

    @Binds
    fun bindGetCardTypeUseCase(getCardTypeUseCaseImpl: GetCardTypeUseCaseImpl): GetCardTypeUseCase

    @Binds
    fun bindSyncCardsUseCase(syncCardsUseCaseImpl: SyncCardsUseCaseImpl): SyncCardsUseCase

    @Binds
    fun bindSyncCardUseCase(syncCardUseCaseImpl: SyncCardUseCaseImpl): SyncCardUseCase

    @Binds
    fun bindGetEmployeesUseCase(getEmployeesUseCaseImpl: GetEmployeesUseCaseImpl): GetEmployeesUseCase

    @Binds
    fun bindSaveCardSolutionUseCase(saveCardSolutionUseCaseImpl: SaveCardSolutionUseCaseImpl): SaveCardSolutionUseCase

    @Binds
    fun bindFirebaseTokenUseCase(getFirebaseTokenUseCaseImpl: GetFirebaseTokenUseCaseImpl): GetFirebaseTokenUseCase

    @Binds
    fun bindSyncFirebaseTokenUseCase(syncFirebaseTokenUseCaseImpl: SyncFirebaseTokenUseCaseImpl):SyncFirebaseTokenUseCase

    @Binds
    fun bindGetFirebaseNotificationUseCase(getFirebaseNotificationUseCaseImpl: GetFirebaseNotificationUseCaseImpl): GetFirebaseNotificationUseCase

    @Binds
    fun bindSendRestorePasswordUseCase(sendRestorePasswordCodeUseCaseImpl: SendRestorePasswordCodeUseCaseImpl): SendRestorePasswordCodeUseCase

    @Binds
    fun bindResetPasswordUseCase(resetPasswordUseCaseImpl: ResetPasswordUseCaseImpl): ResetPasswordUseCase

    @Binds
    fun bindVerifyPasswordCodeUseCase(verifyPasswordCodeUseCaseImpl: VerifyPasswordCodeUseCaseImpl): VerifyPasswordCodeUseCase
}