package com.ih.osm.data.repository.employee

import com.ih.osm.data.database.dao.employee.EmployeeDao
import com.ih.osm.data.database.entities.employee.toDomain
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

class EmployeeRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
        private val dao: EmployeeDao,
        private val authRepository: AuthRepository,
    ) : EmployeeRepository {
        override suspend fun saveAll(list: List<Employee>) {
            list.forEach {
                dao.insert(it.toEntity())
            }
        }

        override suspend fun deleteAll() {
            dao.deleteAll()
        }

        override suspend fun getAll(): List<Employee> {
            return dao.getAll().map { it.toDomain() }
        }

        override suspend fun getAllRemote(): List<Employee> {
            val siteId = authRepository.getSiteId()
            return networkRepository.getRemoteEmployees(siteId)
        }

        override suspend fun getByRole(roleName: String): List<Employee> {
            val siteId = authRepository.getSiteId()
            return networkRepository.getRemoteEmployeesByRole(siteId, roleName)
        }
    }
