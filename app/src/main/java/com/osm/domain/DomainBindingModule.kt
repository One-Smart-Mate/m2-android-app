package com.osm.domain

import com.osm.domain.usecase.card.GetCardDetailUseCase
import com.osm.domain.usecase.card.GetCardDetailUseCaseImpl

import com.osm.domain.usecase.card.GetCardsLevelMachineUseCase
import com.osm.domain.usecase.card.GetCardsLevelMachineUseCaseImpl
import com.osm.domain.usecase.card.GetCardsUseCase
import com.osm.domain.usecase.card.GetCardsUseCaseImpl
import com.osm.domain.usecase.card.GetCardsZoneUseCase
import com.osm.domain.usecase.card.GetCardsZoneUseCaseImpl
import com.osm.domain.usecase.card.SaveCardSolutionUseCase
import com.osm.domain.usecase.card.SaveCardSolutionUseCaseImpl
import com.osm.domain.usecase.card.SaveCardUseCase
import com.osm.domain.usecase.card.SaveCardUseCaseImpl
import com.osm.domain.usecase.card.SyncCardUseCase
import com.osm.domain.usecase.card.SyncCardUseCaseImpl
import com.osm.domain.usecase.card.SyncCardsUseCase
import com.osm.domain.usecase.card.SyncCardsUseCaseImpl
import com.osm.domain.usecase.cardtype.GetCardTypeUseCase
import com.osm.domain.usecase.cardtype.GetCardTypeUseCaseImpl
import com.osm.domain.usecase.cardtype.GetCardTypesUseCase
import com.osm.domain.usecase.cardtype.GetCardTypesUseCaseImpl
import com.osm.domain.usecase.catalogs.CleanCatalogsUseCase
import com.osm.domain.usecase.catalogs.CleanCatalogsUseCaseImpl
import com.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.osm.domain.usecase.catalogs.SyncCatalogsUseCaseImpl
import com.osm.domain.usecase.employee.GetEmployeesUseCase
import com.osm.domain.usecase.employee.GetEmployeesUseCaseImpl
import com.osm.domain.usecase.firebase.GetFirebaseTokenUseCase
import com.osm.domain.usecase.firebase.GetFirebaseTokenUseCaseImpl
import com.osm.domain.usecase.firebase.SyncFirebaseTokenUseCase
import com.osm.domain.usecase.firebase.SyncFirebaseTokenUseCaseImpl
import com.osm.domain.usecase.level.GetLevelsUseCase
import com.osm.domain.usecase.level.GetLevelsUseCaseImpl
import com.osm.domain.usecase.login.LoginUseCase
import com.osm.domain.usecase.login.LoginUseCaseImpl
import com.osm.domain.usecase.logout.LogoutUseCase
import com.osm.domain.usecase.logout.LogoutUseCaseImpl
import com.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.osm.domain.usecase.notifications.GetFirebaseNotificationUseCaseImpl
import com.osm.domain.usecase.password.ResetPasswordUseCase
import com.osm.domain.usecase.password.ResetPasswordUseCaseImpl
import com.osm.domain.usecase.password.SendRestorePasswordCodeUseCase
import com.osm.domain.usecase.password.SendRestorePasswordCodeUseCaseImpl
import com.osm.domain.usecase.password.VerifyPasswordCodeUseCase
import com.osm.domain.usecase.password.VerifyPasswordCodeUseCaseImpl
import com.osm.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.osm.domain.usecase.preclassifier.GetPreclassifiersUseCaseImpl
import com.osm.domain.usecase.priority.GetPrioritiesUseCase
import com.osm.domain.usecase.priority.GetPrioritiesUseCaseImpl
import com.osm.domain.usecase.saveuser.SaveUserUseCase
import com.osm.domain.usecase.saveuser.SaveUserUseCaseImpl
import com.osm.domain.usecase.user.GetUserUseCase
import com.osm.domain.usecase.user.GetUserUseCaseImpl
import com.osm.domain.usecase.user.UpdateTokenUseCase
import com.osm.domain.usecase.user.UpdateTokenUseCaseImpl
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