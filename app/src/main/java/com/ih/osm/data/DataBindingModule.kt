package com.ih.osm.data

import com.ih.osm.data.repository.auth.AuthRepositoryImpl
import com.ih.osm.data.repository.cards.CardRepositoryImpl
import com.ih.osm.data.repository.cardtype.CardTypeRepositoryImpl
import com.ih.osm.data.repository.employee.EmployeeRepositoryImpl
import com.ih.osm.data.repository.evidence.EvidenceRepositoryImpl
import com.ih.osm.data.repository.firebase.FirebaseStorageRepositoryImpl
import com.ih.osm.data.repository.level.LevelRepositoryImpl
import com.ih.osm.data.repository.network.NetworkRepositoryImpl
import com.ih.osm.data.repository.opl.OplRepositoryImpl
import com.ih.osm.data.repository.preclassifier.PreclassifierRepositoryImpl
import com.ih.osm.data.repository.priority.PriorityRepositoryImpl
import com.ih.osm.data.repository.procedure.ProcedureRepositoryImpl
import com.ih.osm.data.repository.session.SessionRepositoryImpl
import com.ih.osm.data.repository.solution.SolutionRepositoryImpl
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.firebase.FirebaseStorageRepository
import com.ih.osm.domain.repository.level.LevelRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.repository.opl.OplRepository
import com.ih.osm.domain.repository.preclassifier.PreclassifierRepository
import com.ih.osm.domain.repository.priority.PriorityRepository
import com.ih.osm.domain.repository.procedure.ProcedureRepository
import com.ih.osm.domain.repository.session.SessionRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataBindingModule {
    @Binds
    fun bindNetworkRepository(networkRepositoryImpl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

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

    @Binds
    fun bindEvidenceRepository(evidenceRepositoryImpl: EvidenceRepositoryImpl): EvidenceRepository

    @Binds
    fun bindSolutionRepository(solutionRepositoryImpl: SolutionRepositoryImpl): SolutionRepository

    @Binds
    fun bindOplRepository(oplRepositoryImpl: OplRepositoryImpl): OplRepository

    @Binds
    fun bindProcedureRepository(procedureRepositoryImpl: ProcedureRepositoryImpl): ProcedureRepository

    @Binds
    fun bindSessionRepository(sessionRepositoryImpl: SessionRepositoryImpl): SessionRepository
}
