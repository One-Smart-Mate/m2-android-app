package com.ih.osm.data.repository.local

import com.ih.osm.data.database.dao.UserDao
import com.ih.osm.data.database.dao.cardtype.CardTypeDao
import com.ih.osm.data.database.dao.employee.EmployeeDao
import com.ih.osm.data.database.dao.evidence.EvidenceDao
import com.ih.osm.data.database.dao.level.LevelDao
import com.ih.osm.data.database.dao.preclassifier.PreclassifierDao
import com.ih.osm.data.database.dao.priority.PriorityDao
import com.ih.osm.data.database.dao.solution.SolutionDao
import com.ih.osm.data.database.entities.card.toDomain
import com.ih.osm.data.database.entities.cardtype.toDomain
import com.ih.osm.data.database.entities.employee.toDomain
import com.ih.osm.data.database.entities.evidence.toDomain
import com.ih.osm.data.database.entities.level.toDomain
import com.ih.osm.data.database.entities.preclassifier.toDomain
import com.ih.osm.data.database.entities.priority.toDomain
import com.ih.osm.data.database.entities.solution.SolutionEntity
import com.ih.osm.data.database.entities.toDomain
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl
@Inject
constructor(
    private val userDao: UserDao,
    private val cardTypeDao: CardTypeDao,
    private val preclassifierDao: PreclassifierDao,
    private val priorityDao: PriorityDao,
    private val levelDao: LevelDao,
    private val evidenceDao: EvidenceDao,
    private val employeeDao: EmployeeDao,
    private val solutionDao: SolutionDao
) : LocalRepository {
    override suspend fun saveUser(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    override suspend fun getUser(): User? {
        return userDao.getUser().toDomain()
    }

    override suspend fun logout(): Int {
        userDao.getUser()?.let {
            return userDao.deleteUser(it)
        }
        return 0
    }

    override suspend fun getSiteId(): String {
        return userDao.getUser()?.siteId.orEmpty()
    }

    override suspend fun getCardTypes(filter: String): List<CardType> {
        return if (filter.isEmpty()) {
            cardTypeDao.getCardTypes().map { it.toDomain() }
        } else {
            cardTypeDao.getCardTypesByMethodology(filter).map { it.toDomain() }
        }
    }

    override suspend fun saveCardTypes(list: List<CardType>) {
        cardTypeDao.deleteCardTypes()
        list.forEach {
            cardTypeDao.insertCardType(it.toEntity())
        }
    }

    override suspend fun getPreclassifiers(): List<Preclassifier> {
        return preclassifierDao.getPreclassifiers().map { it.toDomain() }
    }

    override suspend fun savePreclassifiers(list: List<Preclassifier>) {
        preclassifierDao.deletePreclassifiers()
        list.forEach {
            preclassifierDao.insertPreclassifier(it.toEntity())
        }
    }

    override suspend fun getPriorities(): List<Priority> {
        return priorityDao.getPriorities().map { it.toDomain() }
    }

    override suspend fun savePriorities(list: List<Priority>) {
        priorityDao.deletePriorities()
        list.forEach {
            priorityDao.insertPriority(it.toEntity())
        }
    }

    override suspend fun removeCardTypes() {
        cardTypeDao.deleteCardTypes()
    }

    override suspend fun removePreclassifiers() {
        preclassifierDao.deletePreclassifiers()
    }

    override suspend fun removePriorities() {
        priorityDao.deletePriorities()
    }

    override suspend fun saveLevels(list: List<Level>) {
        levelDao.deleteLevels()
        list.forEach {
            levelDao.insertLevel(it.toEntity())
        }
    }

    override suspend fun getLevels(): List<Level> {
        return levelDao.getLevels().map { it.toDomain() }
    }

    override suspend fun removeLevels() {
        levelDao.deleteLevels()
    }

    override suspend fun getCardType(id: String?): CardType? {
        return cardTypeDao.getCardType(id)?.toDomain()
    }

    override suspend fun getPreclassifier(id: String?): Preclassifier? {
        return preclassifierDao.getPreclassifier(id)?.toDomain()
    }

    override suspend fun getPriority(id: String?): Priority? {
        return priorityDao.getPriority(id)?.toDomain()
    }

    override suspend fun getLevel(id: String?): Level? {
        return levelDao.getLevel(id)?.toDomain()
    }

    override suspend fun saveEvidence(evidence: Evidence): Long {
        return evidenceDao.insertEvidence(evidence.toEntity())
    }

    override suspend fun deleteEvidence(id: String) {
        evidenceDao.deleteEvidence(id)
    }

    override suspend fun deleteEvidences() {
        evidenceDao.deleteEvidences()
    }

    override suspend fun saveEmployees(list: List<Employee>) {
        list.forEach {
            employeeDao.insertEmployee(it.toEntity())
        }
    }

    override suspend fun deleteEmployees() {
        employeeDao.deleteEmployees()
    }

    override suspend fun getEmployees(): List<Employee> {
        return employeeDao.getEmployees().map { it.toDomain() }
    }

    override suspend fun saveSolution(solutionEntity: SolutionEntity) {
        solutionDao.insertSolution(solutionEntity)
    }

    override suspend fun removeSolutions() {
        solutionDao.deleteSolutions()
    }

    override suspend fun getCardSolutions(cardId: String): List<SolutionEntity> {
        return solutionDao.getSolutions(cardId)
    }

    override suspend fun deleteSolutions(cardId: String) {
        solutionDao.deleteSolutionsByCard(cardId)
    }
}
