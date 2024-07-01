package com.ih.m2.data.repository.employee

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.toDomain
import com.ih.m2.data.repository.auth.getErrorMessage
import com.ih.m2.domain.model.Employee
import com.ih.m2.domain.repository.employee.EmployeeRepository
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