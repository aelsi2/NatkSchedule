package aelsi2.natkschedule.data.repositories.network

import aelsi2.natkschedule.data.repositories.LectureRepository
import aelsi2.natkschedule.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate

class NetworkOnlyLectureRepository : LectureRepository {
    override val syncable: Boolean
        get() = false
    override suspend fun getLectures(
        startDate: Instant,
        endDate: Instant,
        group: Group?,
        teacher: Teacher?,
        classroom: Classroom?,
        sync : Boolean
    ) : Result<Iterable<Lecture>> {
        TODO("Not yet implemented")
    }

}