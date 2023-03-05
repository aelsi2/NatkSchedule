package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType

/**
 * Репозиторий атрибутов лекций (препод, аудитория, группа).
 */
interface LectureAttributeRepository {
    /**
     * Поддерживает ли репозиторий синхронизацию.
     */
    val syncable : Boolean

    /**
     * Получить данные об атрибутах лекций по соответствующим идентификаторам лекций.
     * @param keys Список идентификаторов лекций.
     * @param sync Нужно ли обновить данные в репозитории.
     * @return Список атрибутов лекций.
     */
    suspend fun getAttributes(
        keys : List<ScheduleIdentifier>,
        sync : Boolean = true
    ) : Result<List<LectureAttribute>>
    suspend fun getAttributesOfType(
        type : ScheduleType,
        sync : Boolean = true
    ) : Result<List<LectureAttribute>>
}