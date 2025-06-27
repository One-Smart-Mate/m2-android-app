package com.ih.osm.domain

import com.ih.osm.data.repository.cilt.CiltRepositoryImpl
import com.ih.osm.domain.repository.cilt.CiltRepository
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
import com.ih.osm.domain.usecase.card.SyncCardsUseCase
import com.ih.osm.domain.usecase.card.SyncCardsUseCaseImpl
import com.ih.osm.domain.usecase.card.UpdateCardMechanicUseCase
import com.ih.osm.domain.usecase.card.UpdateCardMechanicUseCaseImpl
import com.ih.osm.domain.usecase.cardtype.GetCardTypeUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypeUseCaseImpl
import com.ih.osm.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypesUseCaseImpl
import com.ih.osm.domain.usecase.catalogs.CleanCatalogsUseCase
import com.ih.osm.domain.usecase.catalogs.CleanCatalogsUseCaseImpl
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCase
import com.ih.osm.domain.usecase.catalogs.SyncCatalogsUseCaseImpl
import com.ih.osm.domain.usecase.cilt.CreateCiltEvidenceUseCase
import com.ih.osm.domain.usecase.cilt.CreateCiltEvidenceUseCaseImpl
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCase
import com.ih.osm.domain.usecase.cilt.GetCiltsUseCaseImpl
import com.ih.osm.domain.usecase.cilt.GetOplByIdUseCase
import com.ih.osm.domain.usecase.cilt.GetOplByIdUseCaseImpl
import com.ih.osm.domain.usecase.cilt.StartSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.StartSequenceExecutionUseCaseImpl
import com.ih.osm.domain.usecase.cilt.StopSequenceExecutionUseCase
import com.ih.osm.domain.usecase.cilt.StopSequenceExecutionUseCaseImpl
import com.ih.osm.domain.usecase.employee.GetEmployeesByRoleUseCase
import com.ih.osm.domain.usecase.employee.GetEmployeesByRoleUseCaseImpl
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
import com.ih.osm.domain.usecase.opl.GetOplsByLevelUseCase
import com.ih.osm.domain.usecase.opl.GetOplsByLevelUseCaseImpl
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

//    @Binds
//    fun bindSyncCardUseCase(syncCardUseCaseImpl: SyncCardUseCaseImpl): SyncCardUseCase

    @Binds
    fun bindGetEmployeesUseCase(getEmployeesUseCaseImpl: GetEmployeesUseCaseImpl): GetEmployeesUseCase

    @Binds
    fun bindSaveCardSolutionUseCase(saveCardSolutionUseCaseImpl: SaveCardSolutionUseCaseImpl): SaveCardSolutionUseCase

    @Binds
    fun bindFirebaseTokenUseCase(getFirebaseTokenUseCaseImpl: GetFirebaseTokenUseCaseImpl): GetFirebaseTokenUseCase

    @Binds
    fun bindSyncFirebaseTokenUseCase(syncFirebaseTokenUseCaseImpl: SyncFirebaseTokenUseCaseImpl): SyncFirebaseTokenUseCase

    @Binds
    fun bindGetFirebaseNotificationUseCase(
        getFirebaseNotificationUseCaseImpl: GetFirebaseNotificationUseCaseImpl,
    ): GetFirebaseNotificationUseCase

    @Binds
    fun bindSendRestorePasswordUseCase(
        sendRestorePasswordCodeUseCaseImpl: SendRestorePasswordCodeUseCaseImpl,
    ): SendRestorePasswordCodeUseCase

    @Binds
    fun bindResetPasswordUseCase(resetPasswordUseCaseImpl: ResetPasswordUseCaseImpl): ResetPasswordUseCase

    @Binds
    fun bindVerifyPasswordCodeUseCase(verifyPasswordCodeUseCaseImpl: VerifyPasswordCodeUseCaseImpl): VerifyPasswordCodeUseCase

    @Binds
    fun bindUpdateTokenUseCase(updateTokenUseCaseImpl: UpdateTokenUseCaseImpl): UpdateTokenUseCase

    @Binds
    fun bindGetCardsLevelMachineUseCase(getCardsLevelMachineImpl: GetCardsLevelMachineUseCaseImpl): GetCardsLevelMachineUseCase

    @Binds
    fun bindUpdateCardMechanicUseCase(updateCardMechanicUseCaseImpl: UpdateCardMechanicUseCaseImpl): UpdateCardMechanicUseCase

    @Binds
    fun bindGetEmployeesByRoleUseCase(getEmployeesByRoleUseCaseImpl: GetEmployeesByRoleUseCaseImpl): GetEmployeesByRoleUseCase

    @Binds
    fun bindGetUserCiltDataUseCase(getUserCiltDataUseCaseImpl: GetCiltsUseCaseImpl): GetCiltsUseCase

    @Binds
    fun bindCiltRepository(ciltRepositoryImpl: CiltRepositoryImpl): CiltRepository

    @Binds
    fun bindGetOplByIdUseCase(getOplByIdUseCaseImpl: GetOplByIdUseCaseImpl): GetOplByIdUseCase

    @Binds
    fun bindStartSequenceExecutionUseCase(startExecutionUseCaseImpl: StartSequenceExecutionUseCaseImpl): StartSequenceExecutionUseCase

    @Binds
    fun bindStopSequenceExecutionUseCase(stopExecutionSequenceUseCaseImpl: StopSequenceExecutionUseCaseImpl): StopSequenceExecutionUseCase

    @Binds
    fun bindCreateCiltEvidenceUseCase(createCiltEvidenceUseCaseImpl: CreateCiltEvidenceUseCaseImpl): CreateCiltEvidenceUseCase

    @Binds
    fun bindGetOplsByLevelUseCase(getOplsByLevelUseCaseImpl: GetOplsByLevelUseCaseImpl): GetOplsByLevelUseCase
}
