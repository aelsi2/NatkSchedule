package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.*
import java.time.LocalDate

/**
 * Репозиторий учебных занятий.
 */
interface LectureRepository {
    /**
     * Получить расписание учебных занятий из репозитория.
     * @param fromDate Минимальная дата.
     * @param toDate Максимальные дата.
     * @param identifier Идентификатор расписания (преподаватель, аудитория, группа).
     * @return Последовательность занятий.
     */
    suspend fun getLectures(
        fromDate: LocalDate,
        toDate: LocalDate,
        identifier: ScheduleIdentifier
    ): Result<List<Lecture>>
}
