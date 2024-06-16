package com.ih.m2.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ih.m2.data.database.entities.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM USER_TABLE LIMIT 1")
    suspend fun getUser(): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity): Long
}