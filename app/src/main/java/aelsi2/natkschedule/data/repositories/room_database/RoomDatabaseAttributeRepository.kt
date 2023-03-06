package aelsi2.natkschedule.data.repositories.room_database

import aelsi2.natkschedule.data.room_database.ScheduleDatabase
import aelsi2.natkschedule.data.room_database.daos.ClassroomDao
import aelsi2.natkschedule.data.room_database.daos.GroupDao
import aelsi2.natkschedule.data.room_database.daos.TeacherDao
import aelsi2.natkschedule.data.room_database.model.ClassroomEntity
import aelsi2.natkschedule.data.room_database.model.GroupEntity
import aelsi2.natkschedule.data.room_database.model.TeacherEntity
import aelsi2.natkschedule.data.repositories.WritableAttributeRepository
import aelsi2.natkschedule.model.*
import androidx.room.withTransaction

class RoomDatabaseAttributeRepository(
    private val teacherDao: TeacherDao,
    private val classroomDao: ClassroomDao,
    private val groupDao: GroupDao,
    private val database: ScheduleDatabase
) : WritableAttributeRepository {
    override suspend fun putAttributes(attributes: List<LectureAttribute>) {
        val teacherEntities = ArrayList<TeacherEntity>()
        val classroomEntities = ArrayList<ClassroomEntity>()
        val groupEntities = ArrayList<GroupEntity>()
        attributes.forEach{
            when (it) {
                is Teacher -> teacherEntities.add(TeacherEntity.fromTeacher(it))
                is Classroom -> classroomEntities.add(ClassroomEntity.fromClassroom(it))
                is Group -> groupEntities.add(GroupEntity.fromGroup(it))
            }
        }
        database.withTransaction {
            teacherDao.putTeachers(teacherEntities)
            classroomDao.putClassrooms(classroomEntities)
            groupDao.putGroups(groupEntities)
        }
    }

    override suspend fun deleteUnused(alwaysKeep: List<ScheduleIdentifier>) {
        val teacherIds = ArrayList<String>()
        val classroomIds = ArrayList<String>()
        val groupIds = ArrayList<String>()
        alwaysKeep.forEach{
            when (it.type) {
                ScheduleType.TEACHER -> teacherIds.add(it.stringId)
                ScheduleType.CLASSROOM -> classroomIds.add(it.stringId)
                ScheduleType.GROUP -> groupIds.add(it.stringId)
            }
        }
        database.withTransaction {
            teacherDao.deleteUnused(teacherIds)
            classroomDao.deleteUnused(classroomIds)
            groupDao.deleteUnused(groupIds)
        }
    }

    override suspend fun getAttributesById(
        ids: List<ScheduleIdentifier>
    ): Result<List<LectureAttribute>> {
        val teacherIds = ArrayList<String>()
        val classroomIds = ArrayList<String>()
        val groupIds = ArrayList<String>()
        ids.forEach{
            when (it.type) {
                ScheduleType.TEACHER -> teacherIds.add(it.stringId)
                ScheduleType.CLASSROOM -> classroomIds.add(it.stringId)
                ScheduleType.GROUP -> groupIds.add(it.stringId)
            }
        }
        return Result.success(listOf(
            teacherDao.getTeachers(teacherIds).map { it.toTeacher() },
            classroomDao.getClassrooms(classroomIds).map { it.toClassroom() },
            groupDao.getGroups(groupIds).map { it.toGroup() }
        ).flatten())
    }

    override suspend fun getAllAttributes(
        type: ScheduleType
    ): Result<List<LectureAttribute>> = Result.failure(
        UnsupportedOperationException("Получение всех атрибутов по типу из базы данных Room невозможно.")
    )
}