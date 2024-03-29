package aelsi2.natkschedule.data.repositories.room_database

import aelsi2.natkschedule.data.room_database.ScheduleDatabase
import aelsi2.natkschedule.data.room_database.daos.ClassroomDao
import aelsi2.natkschedule.data.room_database.daos.GroupDao
import aelsi2.natkschedule.data.room_database.daos.TeacherDao
import aelsi2.natkschedule.data.room_database.model.ClassroomEntity
import aelsi2.natkschedule.data.room_database.model.GroupEntity
import aelsi2.natkschedule.data.room_database.model.TeacherEntity
import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.data.room_database.daos.DisciplineDao
import aelsi2.natkschedule.model.*
import androidx.room.withTransaction

class RoomDatabaseScheduleAttributeRepository(
    private val disciplineDao: DisciplineDao,
    private val teacherDao: TeacherDao,
    private val classroomDao: ClassroomDao,
    private val groupDao: GroupDao,
    private val database: ScheduleDatabase
) : WritableScheduleAttributeRepository {
    override suspend fun putAttributes(
        attributes: List<ScheduleAttribute>
    ) = database.withTransaction {
        attributes.forEach{
            when (it) {
                is Teacher -> {
                    teacherDao.putTeacher(TeacherEntity.fromTeacher(it))
                }
                is Classroom -> {
                    classroomDao.putClassroom(ClassroomEntity.fromClassroom(it))
                }
                is Group -> {
                    groupDao.putGroup(GroupEntity.fromGroup(it))
                }
            }
        }
    }

    override suspend fun putAttribute(attribute: ScheduleAttribute) {
        when (attribute) {
            is Teacher -> {
                teacherDao.putTeacher(TeacherEntity.fromTeacher(attribute))
            }
            is Classroom -> {
                classroomDao.putClassroom(ClassroomEntity.fromClassroom(attribute))
            }
            is Group -> {
                groupDao.putGroup(GroupEntity.fromGroup(attribute))
            }
        }
    }

    override suspend fun deleteUnusedExcept(attributesToKeep: List<ScheduleIdentifier>) {
        val teacherIds = ArrayList<String>()
        val classroomIds = ArrayList<String>()
        val groupIds = ArrayList<String>()
        attributesToKeep.forEach{
            when (it.type) {
                ScheduleType.Teacher -> teacherIds.add(it.stringId)
                ScheduleType.Classroom -> classroomIds.add(it.stringId)
                ScheduleType.Group -> groupIds.add(it.stringId)
            }
        }
        database.withTransaction {
            teacherDao.deleteUnused(teacherIds)
            classroomDao.deleteUnused(classroomIds)
            groupDao.deleteUnused(groupIds)
            // Дисциплины не атрибуты, по которым можно получать расписания, но работают очень похоже
            disciplineDao.deleteUnused()
        }
    }

    override suspend fun getAttributesById(
        ids: List<ScheduleIdentifier>
    ): Result<List<ScheduleAttribute>> {
        val teacherIds = ArrayList<String>()
        val classroomIds = ArrayList<String>()
        val groupIds = ArrayList<String>()
        ids.forEach{
            when (it.type) {
                ScheduleType.Teacher -> teacherIds.add(it.stringId)
                ScheduleType.Classroom -> classroomIds.add(it.stringId)
                ScheduleType.Group -> groupIds.add(it.stringId)
            }
        }
        return Result.success(listOf(
            teacherDao.getTeachers(teacherIds).map { it.toTeacher() },
            classroomDao.getClassrooms(classroomIds).map { it.toClassroom() },
            groupDao.getGroups(groupIds).map { it.toGroup() }
        ).flatten())
    }

    override suspend fun getAttributeById(
        id: ScheduleIdentifier
    ): Result<ScheduleAttribute?> = when (id.type) {
        ScheduleType.Teacher -> teacherDao.getTeacher(id.stringId)?.toTeacher()
        ScheduleType.Classroom -> classroomDao.getClassroom(id.stringId)?.toClassroom()
        ScheduleType.Group -> groupDao.getGroup(id.stringId)?.toGroup()
    }.let {
        Result.success(it)
    }

    override suspend fun getAllAttributes(
        type: ScheduleType
    ): Result<List<ScheduleAttribute>> = Result.success(when (type) {
        ScheduleType.Teacher -> teacherDao.getAll().map { it.toTeacher() }
        ScheduleType.Classroom -> classroomDao.getAll().map { it.toClassroom() }
        ScheduleType.Group -> groupDao.getAll().map { it.toGroup() }
    })
}