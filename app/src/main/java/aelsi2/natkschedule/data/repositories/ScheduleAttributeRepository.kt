package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType

/**
 * Репозиторий расписаний (преподов, аудиторий, групп).
 */
interface ScheduleAttributeRepository {

    /**
     * Получить данные об атрибутах расписаний по соответствующим идентификаторам.
     * @param ids Список идентификаторов расписаний.
     * @return Список атрибутов расписаний.
     */
    suspend fun getAttributesById(
        ids: List<ScheduleIdentifier>
    ): Result<List<ScheduleAttribute>>
    /**
     * Получить данные об атрибуте расписания по его идентификатору.
     * @param id Идентификатор расписания.
     * @return Список атрибутов расписаний.
     */
    suspend fun getAttributeById(
        id: ScheduleIdentifier
    ): Result<ScheduleAttribute?>
    /**
     * Получить все атрибуты расписаний типа [type].
     * @param type Тип атрибутов расписаний.
     * @return Список атрибутов расписаний.
     */
    suspend fun getAllAttributes(
        type : ScheduleType
    ): Result<List<ScheduleAttribute>>
}