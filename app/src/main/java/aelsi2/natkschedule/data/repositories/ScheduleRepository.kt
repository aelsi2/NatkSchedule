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
     * @param group Группа, для которой нужно получить расписание занятий (null = не фильтровать по группе).
     * @param teacher Преподаватель, для которого нужно получить расписание занятий (null = не фильтровать по преподавателю).
     * @param classroom Аудитория, для которой нужно получить расписание занятий (null = не фильтровать по аудитории).
     * @return Последовательность занятий.
     */
    suspend fun getSchedule(
        startDate: LocalDate,
        endDate: LocalDate,
        identifier: ScheduleIdentifier,
        sync : Boolean = true
    ): Result<List<Lecture>>
}
