package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.Classroom
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.ScheduleItem
import aelsi2.natkschedule.model.Teacher
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ScheduleRepository {
    suspend fun getSchedule(
        startDate : LocalDate,
        endDate: LocalDate,
        group : Group?,
        teacher: Teacher?,
        classroom: Classroom?,
    ) : Flow<Sequence<ScheduleItem>>
    suspend fun syncAndGetSchedules(
        startDate : LocalDate,
        endDate: LocalDate,
        group : Group?,
        teacher: Teacher?,
        classroom: Classroom?,
    ) : Flow<Sequence<ScheduleItem>>
}