package com.ih.osm.domain.usecase.employee

import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.repository.employee.EmployeeRepository
import com.ih.osm.domain.repository.employee.LocalEmployeeRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetEmployeesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Employee>
}

class GetEmployeesUseCaseImpl
@Inject
constructor(
    private val remoteRepo: EmployeeRepository,
    private val localRepo: LocalEmployeeRepository,
    private val appLocalRepository: LocalRepository
) : GetEmployeesUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Employee> {
        if (syncRemote) {
            val siteId = appLocalRepository.getSiteId()
            val employeeList = remoteRepo.getEmployees(siteId)
            localRepo.saveAll(employeeList)
        }
        return localRepo.getAll()
    }
}
