package com.ih.osm.domain.repository.employee

import com.ih.osm.domain.model.Employee

interface EmployeeRepository {

    suspend fun getEmployees(siteId: String): List<Employee>
}