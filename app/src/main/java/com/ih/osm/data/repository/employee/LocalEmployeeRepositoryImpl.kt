package com.ih.osm.data.repository.employee

import com.ih.osm.data.database.dao.employee.EmployeeDao
import com.ih.osm.data.database.entities.employee.toDomain
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.employee.LocalEmployeeRepository
import javax.inject.Inject

class LocalEmployeeRepositoryImpl @Inject constructor(
    private val dao: EmployeeDao
) : LocalEmployeeRepository {
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
}