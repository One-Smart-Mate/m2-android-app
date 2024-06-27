package com.ih.m2.data.database.dao.employee

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.m2.data.database.entities.employee.EmployeeEntity

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employee_table")
    suspend fun getEmployees(): List<EmployeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employeeEntity: EmployeeEntity): Long

    @Query("DELETE FROM employee_table")
    suspend fun deleteEmployees()
}