package com.ih.osm.data.database.entities.employee

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ih.osm.domain.model.Employee

@Entity(tableName = "employee_table")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "email")
    val email: String,
)

fun EmployeeEntity.toDomain(): Employee =
    Employee(
        id = this.id,
        name = this.name,
        email = this.email,
    )
