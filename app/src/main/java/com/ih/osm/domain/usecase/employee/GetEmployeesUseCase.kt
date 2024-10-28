package com.ih.osm.domain.usecase.employee

import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.employee.LocalEmployeeRepository
import javax.inject.Inject

interface GetEmployeesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Employee>
}

class GetEmployeesUseCaseImpl
@Inject
constructor(
    private val remoteRepo: EmployeeRepository,
    private val localRepo: LocalEmployeeRepository,
    private val authRepository: AuthRepository
) : GetEmployeesUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Employee> {
        if (syncRemote) {
            val siteId = authRepository.getSiteId()
            val employeeList = remoteRepo.getEmployees(siteId)
            localRepo.saveAll(employeeList)
        }
        return localRepo.getAll()
    }
}
