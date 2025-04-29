package com.ih.osm.domain.usecase.employee

import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.repository.employee.EmployeeRepository
import javax.inject.Inject

interface GetEmployeesByRoleUseCase {
    suspend operator fun invoke(roleName: String): List<Employee>
}

class GetEmployeesByRoleUseCaseImpl
    @Inject
    constructor(
        private val employeeRepository: EmployeeRepository,
    ) : GetEmployeesByRoleUseCase {
        override suspend fun invoke(roleName: String): List<Employee> {
            return employeeRepository.getByRole(roleName)
        }
    }
