package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.*
import java.time.LocalDate

/**
 * Репозиторий учебных занятий.
 */
interface ScheduleDayRepository {
    /**
     * Получить расписание учебных занятий из репозитория.
     * @param fromDate Минимальная дата.
     * @param toDate Максимальные дата.
     * @param identifier Идентификатор расписания (преподаватель, аудитория, группа).
     * @return Последовательность занятий.
     */
    suspend fun getDays(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<ScheduleDay>>
}
