package com.ih.osm.domain.usecase.employee

import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.repository.employee.EmployeeRepository
import javax.inject.Inject

interface GetEmployeesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Employee>
}

class GetEmployeesUseCaseImpl
    @Inject
    constructor(
        private val remoteRepo: EmployeeRepository,
    ) : GetEmployeesUseCase {
        override suspend fun invoke(syncRemote: Boolean): List<Employee> {
            if (syncRemote) {
                val employeeList = remoteRepo.getAllRemote()
                remoteRepo.saveAll(employeeList)
            }
            return remoteRepo.getAll()
        }
    }
