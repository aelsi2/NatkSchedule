package aelsi2.natkschedule.data.repositories.room

import aelsi2.natkschedule.data.database.ScheduleDatabase
import aelsi2.natkschedule.data.database.daos.ClassroomDao
import aelsi2.natkschedule.data.database.daos.GroupDao
import aelsi2.natkschedule.data.database.daos.LectureDao
import aelsi2.natkschedule.data.database.daos.TeacherDao
import aelsi2.natkschedule.data.database.model.ClassroomEntity
import aelsi2.natkschedule.data.database.model.GroupEntity
import aelsi2.natkschedule.data.database.model.LectureEntity
import aelsi2.natkschedule.data.database.model.TeacherEntity
import aelsi2.natkschedule.data.preferences.SettingsReader
import aelsi2.natkschedule.data.repositories.ScheduleRepository
import aelsi2.natkschedule.model.*
import androidx.room.withTransaction
import java.time.LocalDate

class RoomCachedScheduleRepository(
    private val lectureDao: LectureDao,
    private val teacherDao: TeacherDao,
    private val classroomDao: ClassroomDao,
    private val groupDao: GroupDao,
    private val database : ScheduleDatabase,
    private val syncRepository : ScheduleRepository,
    private val settingsReader: SettingsReader
) : ScheduleRepository {
    override val syncable: Boolean
        get() = true
    override suspend fun getSchedule(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier,
        sync: Boolean
    ): Result<List<Lecture>> {
        if (sync) {
            val syncResult = syncRepository.getSchedule(startDate, endDate, identifier)
            syncResult.fold(
                onSuccess = {
                    putLectures(startDate, endDate, identifier, it)
                    return Result.success(it)
                },
                onFailure = {
                    return Result.failure(it)
                }
            )
        }
        else {
            return Result.success(loadLectures(startDate, endDate, identifier))
        }
    }
    private suspend fun loadLectures(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier
    ) : List<Lecture> {
        val teacherId = if (identifier.type == ScheduleType.TEACHER) { identifier.stringId } else { null }
        val groupId = if (identifier.type == ScheduleType.GROUP) { identifier.stringId } else { null }
        val classroomId = if (identifier.type == ScheduleType.CLASSROOM) { identifier.stringId } else { null }
        return lectureDao.getPopulatedLectures(startDate, endDate,
            teacherId,
            groupId,
            classroomId
        ).map {
            it.toLecture()
        }
    }
    private suspend fun putLectures(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier,
        lectures : List<Lecture>
    ) {
        val teacherEntities = ArrayList<TeacherEntity>()
        val groupEntities = ArrayList<GroupEntity>()
        val classroomEntities = ArrayList<ClassroomEntity>()
        val lectureEntities = lectures.map {
            val entity = LectureEntity.fromLecture(it)
            if (it.teacher != null) {
                teacherEntities.add(TeacherEntity.fromTeacher(it.teacher))
            }
            if (it.group != null) {
                groupEntities.add(GroupEntity.fromGroup(it.group))
            }
            if (it.classroom != null) {
                classroomEntities.add(ClassroomEntity.fromClassroom(it.classroom))
            }
            return@map entity
        }
        val teacherId = if (identifier.type == ScheduleType.TEACHER) { identifier.stringId } else { null }
        val groupId = if (identifier.type == ScheduleType.GROUP) { identifier.stringId } else { null }
        val classroomId = if (identifier.type == ScheduleType.CLASSROOM) { identifier.stringId } else { null }
        database.withTransaction {
            lectureDao.deleteLecturesBetween(startDate, endDate, teacherId, groupId, classroomId)
            teacherDao.putTeachers(teacherEntities)
            groupDao.putGroups(groupEntities)
            classroomDao.putClassrooms(classroomEntities)
            lectureDao.putLectures(lectureEntities)
        }
    }
}