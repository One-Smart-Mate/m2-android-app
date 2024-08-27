package com.osm.data

import com.osm.data.repository.auth.AuthRepositoryImpl
import com.osm.data.repository.cards.CardRepositoryImpl
import com.osm.data.repository.cardtype.CardTypeRepositoryImpl
import com.osm.data.repository.employee.EmployeeRepositoryImpl
import com.osm.data.repository.firebase.FirebaseStorageRepositoryImpl
import com.osm.data.repository.level.LevelRepositoryImpl
import com.osm.data.repository.local.LocalRepositoryImpl
import com.osm.data.repository.preclassifier.PreclassifierRepositoryImpl
import com.osm.data.repository.priority.PriorityRepositoryImpl
import com.osm.domain.repository.auth.AuthRepository
import com.osm.domain.repository.cards.CardRepository
import com.osm.domain.repository.cardtype.CardTypeRepository
import com.osm.domain.repository.employee.EmployeeRepository
import com.osm.domain.repository.firebase.FirebaseStorageRepository
import com.osm.domain.repository.level.LevelRepository
import com.osm.domain.repository.local.LocalRepository
import com.osm.domain.repository.preclassifier.PreclassifierRepository
import com.osm.domain.repository.priority.PriorityRepository
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