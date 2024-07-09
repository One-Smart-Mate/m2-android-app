package com.ih.m2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ih.m2.data.database.dao.UserDao
import com.ih.m2.data.database.dao.card.CardDao
import com.ih.m2.data.database.dao.cardtype.CardTypeDao
import com.ih.m2.data.database.dao.employee.EmployeeDao
import com.ih.m2.data.database.dao.evidence.EvidenceDao
import com.ih.m2.data.database.dao.level.LevelDao
import com.ih.m2.data.database.dao.preclassifier.PreclassifierDao
import com.ih.m2.data.database.dao.priority.PriorityDao
import com.ih.m2.data.database.entities.UserEntity
import com.ih.m2.data.database.entities.card.CardEntity
import com.ih.m2.data.database.entities.cardtype.CardTypeEntity
import com.ih.m2.data.database.entities.employee.EmployeeEntity
import com.ih.m2.data.database.entities.evidence.EvidenceEntity
import com.ih.m2.data.database.entities.level.LevelEntity
import com.ih.m2.data.database.entities.preclassifier.PreclassifierEntity
import com.ih.m2.data.database.entities.priority.PriorityEntity


@Database(
    entities = [
        UserEntity::class,
        CardEntity::class,
        CardTypeEntity::class,
        PreclassifierEntity::class,
        PriorityEntity::class,
        LevelEntity::class,
        EvidenceEntity::class,
        EmployeeEntity::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    abstract fun getCardDao(): CardDao

    abstract fun getCardTypeDao(): CardTypeDao

    abstract fun getPreclassifierDao(): PreclassifierDao

    abstract fun getPriorityDao(): PriorityDao

    abstract fun getLevelDao(): LevelDao

    abstract fun getEvidenceDao(): EvidenceDao

    abstract fun getEmployeeDao(): EmployeeDao

}