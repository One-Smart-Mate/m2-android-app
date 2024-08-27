package com.osm.domain.usecase.employee


import com.osm.domain.model.Employee
import com.osm.domain.repository.employee.EmployeeRepository
import com.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetEmployeesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Employee>
}

class GetEmployeesUseCaseImpl @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val localRepository: LocalRepository
) : GetEmployeesUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Employee> {
        if (syncRemote) {
            val siteId = localRepository.getSiteId()
            val employeeList = employeeRepository.getEmployees(siteId)
            localRepository.saveEmployees(employeeList)
        }
        return localRepository.getEmployees()
    }
}