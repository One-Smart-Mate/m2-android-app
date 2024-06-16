package com.ih.m2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ih.m2.data.database.dao.UserDao
import com.ih.m2.data.database.entities.UserEntity


@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getUserDao(): UserDao

}