package com.ih.m2.data

import com.ih.m2.data.repository.auth.AuthRepositoryImpl
import com.ih.m2.data.repository.cards.CardRepositoryImpl
import com.ih.m2.data.repository.cardtype.CardTypeRepositoryImpl
import com.ih.m2.data.repository.employee.EmployeeRepositoryImpl
import com.ih.m2.data.repository.firebase.FirebaseStorageRepositoryImpl
import com.ih.m2.data.repository.level.LevelRepositoryImpl
import com.ih.m2.data.repository.local.LocalRepositoryImpl
import com.ih.m2.data.repository.preclassifier.PreclassifierRepositoryImpl
import com.ih.m2.data.repository.priority.PriorityRepositoryImpl
import com.ih.m2.domain.repository.auth.AuthRepository
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.cardtype.CardTypeRepository
import com.ih.m2.domain.repository.employee.EmployeeRepository
import com.ih.m2.domain.repository.firebase.FirebaseStorageRepository
import com.ih.m2.domain.repository.level.LevelRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.repository.preclassifier.PreclassifierRepository
import com.ih.m2.domain.repository.priority.PriorityRepository
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
    fun bindPreclassifierRepository(preclassifierRepositoryImpl: PreclassifierRepositoryImpl): PreclassifierRepository

    @Binds
    fun bindPriorityRepository(priorityRepositoryImpl: PriorityRepositoryImpl): PriorityRepository

    @Binds
    fun bindLevelRepository(levelRepositoryImpl: LevelRepositoryImpl): LevelRepository

    @Binds
    fun bindFirebaseRepository(firebaseStorageRepositoryImpl: FirebaseStorageRepositoryImpl): FirebaseStorageRepository

    @Binds
    fun bindEmployeeRepository(employeeRepositoryImpl: EmployeeRepositoryImpl): EmployeeRepository
}