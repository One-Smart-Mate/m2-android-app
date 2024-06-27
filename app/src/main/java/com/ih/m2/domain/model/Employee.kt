package com.ih.m2.domain.model

import com.ih.m2.data.database.entities.employee.EmployeeEntity

data class Employee(val id: String, val name: String, val email: String)

fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        id = this.id,
        name = this.name,
        email = this.email
    )
}