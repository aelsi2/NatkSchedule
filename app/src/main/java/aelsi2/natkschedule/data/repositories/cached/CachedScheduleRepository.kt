package aelsi2.natkschedule.data.repositories.cached

import aelsi2.natkschedule.data.database.daos.ClassroomDao
import aelsi2.natkschedule.data.database.daos.GroupDao
import aelsi2.natkschedule.data.database.daos.LectureDao
import aelsi2.natkschedule.data.database.daos.TeacherDao
import aelsi2.natkschedule.data.repositories.ScheduleRepository
import aelsi2.natkschedule.model.*
import java.time.Instant

class CachedScheduleRepository(
    private val lectureDao: LectureDao,
    private val teacherDao: TeacherDao,
    private val classroomDao: ClassroomDao,
    private val groupDao: GroupDao,
    private val syncRepository : ScheduleRepository
) : ScheduleRepository {
    override val syncable: Boolean
        get() = true
    override suspend fun getSchedule(
        startDate: Instant,
        endDate: Instant,
        identifier : ScheduleIdentifier,
        sync : Boolean
    ): Result<Iterable<Lecture>> {
        TODO("Not yet implemented")
    }
}