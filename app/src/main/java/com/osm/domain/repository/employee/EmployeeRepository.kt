package com.osm.domain.repository.employee

import com.osm.domain.model.Employee

interface EmployeeRepository {

    suspend fun getEmployees(siteId: String): List<Employee>
}