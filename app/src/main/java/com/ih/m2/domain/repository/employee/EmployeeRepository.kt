package com.ih.m2.domain.repository.employee

import com.ih.m2.domain.model.Employee

interface EmployeeRepository {

    suspend fun getEmployees(siteId: String): List<Employee>
}