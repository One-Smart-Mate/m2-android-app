package com.ih.m2.data.model

import com.ih.m2.domain.model.Employee
import com.ih.m2.domain.model.User

data class GetEmployeesResponse(val data: List<Employee>, val status: Int, val message: String)

fun GetEmployeesResponse.toDomain() = this.data