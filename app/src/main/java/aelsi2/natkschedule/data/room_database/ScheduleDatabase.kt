package aelsi2.natkschedule.data.room_database

import aelsi2.natkschedule.data.room_database.converters.DateConverter
import aelsi2.natkschedule.data.room_database.converters.ScheduleIdentifierConverter
import aelsi2.natkschedule.data.room_database.converters.TimeConverter
import aelsi2.natkschedule.data.room_database.daos.*
import aelsi2.natkschedule.data.room_database.model.*
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ClassroomEntity::class,
        GroupEntity::class,
        TeacherEntity::class,
        LectureEntity::class,
        DisciplineEntity::class,
        ScheduleDayEntity::class,
        LectureDataEntity::class
    ],
    version = 5
)
@TypeConverters(DateConverter::class, TimeConverter::class, ScheduleIdentifierConverter::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun classroomDao() : ClassroomDao
    abstract fun disciplineDao() : DisciplineDao
    abstract fun teacherDao() : TeacherDao
    abstract fun groupDao() : GroupDao
    abstract fun lectureDao() : ScheduleDao
}