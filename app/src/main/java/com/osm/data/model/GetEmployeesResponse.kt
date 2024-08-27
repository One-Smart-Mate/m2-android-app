package com.osm.data.model

import com.osm.domain.model.Employee
import com.osm.domain.model.User

data class GetEmployeesResponse(val data: List<Employee>, val status: Int, val message: String)

fun GetEmployeesResponse.toDomain() = this.data