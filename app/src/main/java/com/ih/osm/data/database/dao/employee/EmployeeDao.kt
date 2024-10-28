package com.ih.osm.data.database.dao.employee

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.osm.data.database.entities.employee.EmployeeEntity

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employee_table")
    suspend fun getAll(): List<EmployeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employeeEntity: EmployeeEntity): Long

    @Query("DELETE FROM employee_table")
    suspend fun deleteAll()
}
