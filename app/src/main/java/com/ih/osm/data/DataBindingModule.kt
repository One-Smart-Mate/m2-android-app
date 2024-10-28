package com.ih.osm.data

import com.ih.osm.data.repository.auth.AuthRepositoryImpl
import com.ih.osm.data.repository.cards.CardRepositoryImpl
import com.ih.osm.data.repository.cards.LocalCardRepositoryImpl
import com.ih.osm.data.repository.cardtype.CardTypeRepositoryImpl
import com.ih.osm.data.repository.cardtype.LocalCardTypeRepositoryImpl
import com.ih.osm.data.repository.employee.EmployeeRepositoryImpl
import com.ih.osm.data.repository.firebase.FirebaseStorageRepositoryImpl
import com.ih.osm.data.repository.level.LevelRepositoryImpl
import com.ih.osm.data.repository.local.LocalRepositoryImpl
import com.ih.osm.data.repository.preclassifier.PreclassifierRepositoryImpl
import com.ih.osm.data.repository.priority.PriorityRepositoryImpl
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import com.ih.osm.domain.repository.cardtype.LocalCardTypeRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.repository.level.LevelRepository
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.domain.repository.preclassifier.PreclassifierRepository
import com.ih.osm.domain.repository.priority.PriorityRepository
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

    @Binds
    fun bindCardRepository(cardRepositoryImpl: CardRepositoryImpl): CardRepository

    @Binds
    fun bindCardTypeRepository(cardTypeRepositoryImpl: CardTypeRepositoryImpl): CardTypeRepository

    @Binds
    fun bindPreclassifierRepository(
        preclassifierRepositoryImpl: PreclassifierRepositoryImpl
    ): PreclassifierRepository

    @Binds
    fun bindPriorityRepository(priorityRepositoryImpl: PriorityRepositoryImpl): PriorityRepository

    @Binds
    fun bindLevelRepository(levelRepositoryImpl: LevelRepositoryImpl): LevelRepository

    @Binds
    fun bindFirebaseRepository(
        firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl
    ): FirebaseStorageRepository

    @Binds
    fun bindEmployeeRepository(employeeRepositoryImpl: EmployeeRepositoryImpl): EmployeeRepository

    @Binds
    fun bindLocalCardRepository(
        localCardRepositoryImpl: LocalCardRepositoryImpl
    ): LocalCardRepository

    @Binds
    fun bindLocalCardTypeRepository(
        localCardTypeRepositoryImpl: LocalCardTypeRepositoryImpl
    ): LocalCardTypeRepository
}
