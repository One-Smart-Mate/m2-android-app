package com.osm.domain.model

import com.osm.data.database.entities.employee.EmployeeEntity

data class Employee(val id: String, val name: String, val email: String)

fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        id = this.id,
        name = this.name,
        email = this.email
    )
}