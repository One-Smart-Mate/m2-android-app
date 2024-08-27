package com.osm.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.osm.data.database.dao.UserDao
import com.osm.data.database.dao.card.CardDao
import com.osm.data.database.dao.cardtype.CardTypeDao
import com.osm.data.database.dao.employee.EmployeeDao
import com.osm.data.database.dao.evidence.EvidenceDao
import com.osm.data.database.dao.level.LevelDao
import com.osm.data.database.dao.preclassifier.PreclassifierDao
import com.osm.data.database.dao.priority.PriorityDao
import com.osm.data.database.entities.UserEntity
import com.osm.data.database.entities.card.CardEntity
import com.osm.data.database.entities.cardtype.CardTypeEntity
import com.osm.data.database.entities.employee.EmployeeEntity
import com.osm.data.database.entities.evidence.EvidenceEntity
import com.osm.data.database.entities.level.LevelEntity
import com.osm.data.database.entities.preclassifier.PreclassifierEntity
import com.osm.data.database.entities.priority.PriorityEntity


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
    version = 1
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