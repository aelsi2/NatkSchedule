package aelsi2.natkschedule.data.repositories.cached

import aelsi2.natkschedule.data.database.daos.ClassroomDao
import aelsi2.natkschedule.data.database.daos.GroupDao
import aelsi2.natkschedule.data.database.daos.LectureDao
import aelsi2.natkschedule.data.database.daos.TeacherDao
import aelsi2.natkschedule.data.repositories.LectureRepository
import aelsi2.natkschedule.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate

class CachedLectureRepository(
    private val lectureDao: LectureDao,
    private val teacherDao: TeacherDao,
    private val classroomDao: ClassroomDao,
    private val groupDao: GroupDao,
    private val syncRepository : LectureRepository
) : LectureRepository {
    override val syncable: Boolean
        get() = true
    override suspend fun getLectures(
        startDate: Instant,
        endDate: Instant,
        group: Group?,
        teacher: Teacher?,
        classroom: Classroom?,
        sync : Boolean
    ): Result<Iterable<Lecture>> {
        TODO("Not yet implemented")
    }
}