package com.ih.osm.domain

import com.ih.osm.domain.usecase.card.GetCardDetailUseCase
import com.ih.osm.domain.usecase.card.GetCardDetailUseCaseImpl

import com.ih.osm.domain.usecase.card.GetCardsLevelMachineUseCase
import com.ih.osm.domain.usecase.card.GetCardsLevelMachineUseCaseImpl
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.card.GetCardsUseCaseImpl
import com.ih.osm.domain.usecase.card.GetCardsZoneUseCase
import com.ih.osm.domain.usecase.card.GetCardsZoneUseCaseImpl
import com.ih.osm.domain.usecase.card.SaveCardSolutionUseCase
import com.ih.osm.domain.usecase.card.SaveCardSolutionUseCaseImpl
import com.ih.osm.domain.usecase.card.SaveCardUseCase
import com.ih.osm.domain.usecase.card.SaveCardUseCaseImpl
import com.ih.osm.domain.usecase.card.SyncCardUseCase
import com.ih.osm.domain.usecase.card.SyncCardUseCaseImpl
import com.ih.osm.domain.usecase.card.SyncCardsUseCase
import com.ih.osm.domain.usecase.card.SyncCardsUseCaseImpl
import com.ih.osm.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypeUseCaseImpl
import com.ih.osm.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypesUseCaseImpl
import com.ih.osm.domain.usecase.catalogs.CleanCatalogsUseCase
import com.ih.osm.domain.usecase.catalogs.CleanCatalogsUseCaseImpl
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCaseImpl
import com.ih.osm.domain.usecase.employee.GetEmployeesUseCase
import com.ih.osm.domain.usecase.employee.GetEmployeesUseCaseImpl
import com.ih.osm.domain.usecase.firebase.GetFirebaseTokenUseCase
import com.ih.osm.domain.usecase.firebase.GetFirebaseTokenUseCaseImpl
import com.ih.osm.domain.usecase.firebase.SyncFirebaseTokenUseCase
import com.ih.osm.domain.usecase.firebase.SyncFirebaseTokenUseCaseImpl
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.domain.usecase.level.GetLevelsUseCaseImpl
import com.ih.osm.domain.usecase.login.LoginUseCase
import com.ih.osm.domain.usecase.login.LoginUseCaseImpl
import com.ih.osm.domain.usecase.logout.LogoutUseCase
import com.ih.osm.domain.usecase.logout.LogoutUseCaseImpl
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCaseImpl
import com.ih.osm.domain.usecase.password.ResetPasswordUseCase
import com.ih.osm.domain.usecase.password.ResetPasswordUseCaseImpl
import com.ih.osm.domain.usecase.password.SendRestorePasswordCodeUseCase
import com.ih.osm.domain.usecase.password.SendRestorePasswordCodeUseCaseImpl
import com.ih.osm.domain.usecase.password.VerifyPasswordCodeUseCase
import com.ih.osm.domain.usecase.password.VerifyPasswordCodeUseCaseImpl
import com.ih.osm.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.osm.domain.usecase.preclassifier.GetPreclassifiersUseCaseImpl
import com.ih.osm.domain.usecase.priority.GetPrioritiesUseCase
import com.ih.osm.domain.usecase.priority.GetPrioritiesUseCaseImpl
import com.ih.osm.domain.usecase.saveuser.SaveUserUseCase
import com.ih.osm.domain.usecase.saveuser.SaveUserUseCaseImpl
import com.ih.osm.domain.usecase.user.GetUserUseCase
import com.ih.osm.domain.usecase.user.GetUserUseCaseImpl
import com.ih.osm.domain.usecase.user.UpdateTokenUseCase
import com.ih.osm.domain.usecase.user.UpdateTokenUseCaseImpl
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

    @Binds
    fun bindUpdateTokenUseCase(updateTokenUseCaseImpl: UpdateTokenUseCaseImpl): UpdateTokenUseCase

    @Binds
    fun bindGetCardsLevelMachine(getCardsLevelMachineImpl: GetCardsLevelMachineUseCaseImpl): GetCardsLevelMachineUseCase
}