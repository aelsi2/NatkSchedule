package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.model.ScheduleIdentifier
import java.time.LocalDate

interface WritableScheduleDayRepository : ScheduleDayRepository {
    suspend fun deleteAllBefore(dateExclusive : LocalDate)
    suspend fun putDays(
        scheduleIdentifier: ScheduleIdentifier,
        days: List<ScheduleDay>
    )
    suspend fun deleteAllExcept(schedulesToKeep: List<ScheduleIdentifier>)
    suspend fun deleteAll()
}