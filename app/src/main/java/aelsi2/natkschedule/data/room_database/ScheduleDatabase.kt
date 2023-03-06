package aelsi2.natkschedule.data.room_database

import aelsi2.natkschedule.data.room_database.converters.DateConverter
import aelsi2.natkschedule.data.room_database.converters.TimeConverter
import aelsi2.natkschedule.data.room_database.daos.ClassroomDao
import aelsi2.natkschedule.data.room_database.daos.GroupDao
import aelsi2.natkschedule.data.room_database.daos.LectureDao
import aelsi2.natkschedule.data.room_database.daos.TeacherDao
import aelsi2.natkschedule.data.room_database.model.ClassroomEntity
import aelsi2.natkschedule.data.room_database.model.GroupEntity
import aelsi2.natkschedule.data.room_database.model.LectureEntity
import aelsi2.natkschedule.data.room_database.model.TeacherEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ClassroomEntity::class,
        GroupEntity::class,
        TeacherEntity::class,
        LectureEntity::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class, TimeConverter::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun classroomDao() : ClassroomDao
    abstract fun teacherDao() : TeacherDao
    abstract fun groupDao() : GroupDao
    abstract fun lectureDao() : LectureDao
}