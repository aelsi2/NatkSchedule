package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.*
import java.time.Instant

/**
 * Репозиторий учебных занятий.
 */
interface LectureRepository {
    /**
     * Поддерживает ли репозиторий синхронизацию.
     */
    val syncable : Boolean
    /**
     * Получить расписание учебных занятий из репозитория.
     * @param startDate Минимальная дата/время начала занятия.
     * @param endDate Максимальные дата/время начала занятия.
     * @param group Группа, для которой нужно получить расписание занятий (null = не фильтровать по группе).
     * @param teacher Преподаватель, для которого нужно получить расписание занятий (null = не фильтровать по преподавателю).
     * @param classroom Аудитория, для которой нужно получить расписание занятий (null = не фильтровать по аудитории).
     * @param sync Нужно ли выполнить синхронизацию (если поддерживается).
     * @return Последовательность занятий.
     */
    suspend fun getLectures(
        startDate : Instant,
        endDate: Instant,
        group : Group? = null,
        teacher: Teacher? = null,
        classroom: Classroom? = null,
        sync : Boolean = true
    ) : Result<Iterable<Lecture>>
}
