package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.*
import java.time.LocalDate

/**
 * Репозиторий учебных занятий.
 */
interface ScheduleRepository {
    /**
     * Поддерживает ли репозиторий синхронизацию.
     */
    val syncable : Boolean
    /**
     * Получить расписание учебных занятий из репозитория.
     * @param startDate Минимальная дата.
     * @param endDate Максимальные дата.
     * @param identifier Идентификатор расписания (преподаватель, аудитория, группа).
     * @param sync Нужно ли обновить данные в репозитории.
     * @return Последовательность занятий.
     */
    suspend fun getSchedule(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier,
        sync : Boolean = true
    ): Result<List<Lecture>>
}
