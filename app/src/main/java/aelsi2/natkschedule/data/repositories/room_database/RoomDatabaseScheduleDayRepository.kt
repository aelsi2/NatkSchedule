package aelsi2.natkschedule.data.repositories.room_database

import aelsi2.natkschedule.data.room_database.ScheduleDatabase
import aelsi2.natkschedule.data.repositories.WritableScheduleDayRepository
import aelsi2.natkschedule.data.room_database.daos.*
import aelsi2.natkschedule.data.room_database.model.*
import aelsi2.natkschedule.model.*
import android.util.Log
import androidx.room.withTransaction
import java.time.LocalDate

class RoomDatabaseScheduleDayRepository(
    private val scheduleDao: ScheduleDao,
    private val disciplineDao: DisciplineDao,
    private val teacherDao: TeacherDao,
    private val classroomDao: ClassroomDao,
    private val groupDao: GroupDao,
    private val database : ScheduleDatabase
) : WritableScheduleDayRepository {
    override suspend fun getDays(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<ScheduleDay>> {
        return Result.success(
            scheduleDao.getDaysWithLectures(
                fromDate,
                toDate,
                identifier
            ).map { it.toScheduleDay() }
        )
    }

    override suspend fun deleteAllBefore(dateExclusive: LocalDate) {
        scheduleDao.deleteDaysBefore(dateExclusive)
    }

    override suspend fun putDays(
        scheduleIdentifier: ScheduleIdentifier,
        days: List<ScheduleDay>
    ) = database.withTransaction {
        days.forEach { day ->
            scheduleDao.deleteDay(day.date, scheduleIdentifier)
            val dayEntity = scheduleDao.getScheduleDay(scheduleDao.putDay(
                ScheduleDayEntity.fromDay(day, scheduleIdentifier)
            ))
            day.lectures.forEach { lecture ->
                if (lecture.discipline != null) {
                    disciplineDao.putDiscipline(
                        DisciplineEntity.fromDiscipline(lecture.discipline)
                    )
                }
                val lectureEntity = scheduleDao.getLecture(scheduleDao.putLecture(
                    LectureEntity.fromLecture(lecture, dayEntity.scheduleDayId)
                ))
                lecture.data.forEach {data ->
                    if (data.teacher != null) {
                        teacherDao.putTeacher(
                            TeacherEntity.fromTeacher(data.teacher)
                        )
                    }
                    if (data.classroom != null) {
                        classroomDao.putClassroom(
                            ClassroomEntity.fromClassroom(data.classroom)
                        )
                    }
                    if (data.group != null) {
                        groupDao.putGroup(
                            GroupEntity.fromGroup(data.group)
                        )
                    }
                    scheduleDao.putLectureData(
                        LectureDataEntity.fromLectureData(data, lectureEntity.lectureId)
                    )
                }
            }
        }
    }

    override suspend fun deleteAllExcept(schedulesToKeep: List<ScheduleIdentifier>) {
        scheduleDao.deleteAllExcept(schedulesToKeep)
    }

    override suspend fun deleteAll() {
        scheduleDao.deleteAll()
    }
}