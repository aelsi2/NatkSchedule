package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType

/**
 * Репозиторий атрибутов лекций (препод, аудитория, группа).
 */
interface AttributeRepository {

    /**
     * Получить данные об атрибутах лекций по соответствующим идентификаторам лекций.
     * @param ids Список идентификаторов лекций.
     * @return Список атрибутов лекций.
     */
    suspend fun getAttributesById(
        ids: List<ScheduleIdentifier>
    ): Result<List<LectureAttribute>>

    suspend fun getAllAttributes(
        type : ScheduleType
    ): Result<List<LectureAttribute>>
}