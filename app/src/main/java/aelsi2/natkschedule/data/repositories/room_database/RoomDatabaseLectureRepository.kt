package aelsi2.natkschedule.data.repositories.room_database

import aelsi2.natkschedule.data.room_database.ScheduleDatabase
import aelsi2.natkschedule.data.room_database.daos.ClassroomDao
import aelsi2.natkschedule.data.room_database.daos.GroupDao
import aelsi2.natkschedule.data.room_database.daos.LectureDao
import aelsi2.natkschedule.data.room_database.daos.TeacherDao
import aelsi2.natkschedule.data.room_database.model.ClassroomEntity
import aelsi2.natkschedule.data.room_database.model.GroupEntity
import aelsi2.natkschedule.data.room_database.model.LectureEntity
import aelsi2.natkschedule.data.room_database.model.TeacherEntity
import aelsi2.natkschedule.data.repositories.WritableLectureRepository
import aelsi2.natkschedule.model.*
import androidx.room.withTransaction
import java.time.LocalDate

class RoomDatabaseLectureRepository(
    private val lectureDao: LectureDao,
    private val teacherDao: TeacherDao,
    private val classroomDao: ClassroomDao,
    private val groupDao: GroupDao,
    private val database : ScheduleDatabase
) : WritableLectureRepository {
    override suspend fun getLectures(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<Lecture>> {
        val teacherId = if (identifier.type == ScheduleType.TEACHER) { identifier.stringId } else { null }
        val groupId = if (identifier.type == ScheduleType.GROUP) { identifier.stringId } else { null }
        val classroomId = if (identifier.type == ScheduleType.CLASSROOM) { identifier.stringId } else { null }
        return Result.success(
            lectureDao.getPopulatedLectures(fromDate,
                toDate,
                teacherId,
                groupId,
                classroomId
            ).map {
                it.toLecture()
            }
        )
    }

    override suspend fun deleteAllBefore(dateExclusive: LocalDate) {
        lectureDao.deleteLecturesBefore(dateExclusive)
    }

    override suspend fun putLectures(
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