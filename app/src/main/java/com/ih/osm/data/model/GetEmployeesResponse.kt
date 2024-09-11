package com.ih.osm.data.model

import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.User

data class GetEmployeesResponse(val data: List<Employee>, val status: Int, val message: String)

fun GetEmployeesResponse.toDomain() = this.data