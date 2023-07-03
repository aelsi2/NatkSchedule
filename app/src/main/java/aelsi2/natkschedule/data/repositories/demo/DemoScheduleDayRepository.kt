package aelsi2.natkschedule.data.repositories.demo

import aelsi2.natkschedule.data.repositories.ScheduleDayRepository
import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.model.ScheduleIdentifier
import java.time.LocalDate

class DemoScheduleDayRepository(
    private val repository: ScheduleDayRepository
) : ScheduleDayRepository {
    override suspend fun getDays(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<ScheduleDay>> = when (identifier) {
        DEMO_GROUP.scheduleIdentifier, DEMO_CLASSROOM.scheduleIdentifier, DEMO_TEACHER.scheduleIdentifier -> {
            val days = mutableListOf<ScheduleDay>()
            var currentDate = fromDate
            while (currentDate <= toDate) {
                days.add(createDemoScheduleDay(currentDate))
                currentDate = currentDate.plusDays(1)
            }
            Result.success(days)
        }
        else -> repository.getDays(fromDate, toDate, identifier)
    }
}