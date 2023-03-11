package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.ScheduleIdentifier
import java.time.LocalDate

interface WritableLectureRepository : LectureRepository {
    suspend fun deleteAllBefore(dateExclusive : LocalDate)
    suspend fun putLectures(
        startDate : LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier,
        lectures : List<Lecture>
    )
    suspend fun cleanSchedules(schedulesToKeep: List<ScheduleIdentifier>)
}