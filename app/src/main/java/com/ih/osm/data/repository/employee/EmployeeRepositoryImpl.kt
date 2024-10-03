package com.ih.osm.data.repository.employee

import com.ih.osm.data.api.ApiService
import com.ih.osm.data.model.toDomain
import com.ih.osm.data.repository.auth.getErrorMessage
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.repository.employee.EmployeeRepository
import javax.inject.Inject

class EmployeeRepositoryImpl
@Inject
constructor(
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
