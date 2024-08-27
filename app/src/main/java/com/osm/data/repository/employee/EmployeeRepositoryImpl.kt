package com.osm.data.repository.employee

import com.osm.data.api.ApiService
import com.osm.data.model.toDomain
import com.osm.data.repository.auth.getErrorMessage
import com.osm.domain.model.Employee
import com.osm.domain.repository.employee.EmployeeRepository
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : EmployeeRepository {

    override suspend fun getEmployees(siteId: String): List<Employee> {
        val response = apiService.getEmployees(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}