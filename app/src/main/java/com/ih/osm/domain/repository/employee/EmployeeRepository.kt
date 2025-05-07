package com.ih.osm.domain.repository.employee

import com.ih.osm.domain.model.Employee

interface EmployeeRepository {
    suspend fun saveAll(list: List<Employee>)

    suspend fun deleteAll()

    suspend fun getAll(): List<Employee>

    suspend fun getAllRemote(): List<Employee>

    suspend fun getByRole(roleName: String): List<Employee>
}
