package com.ih.osm.domain.repository.employee

import com.ih.osm.domain.model.Employee

interface LocalEmployeeRepository {

    suspend fun saveAll(list: List<Employee>)

    suspend fun deleteAll()

    suspend fun getAll(): List<Employee>
}
