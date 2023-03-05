package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType

/**
 * Репозиторий атрибутов лекций (группа, препод, аудитория).
 */
interface LectureAttributeRepository {
    /**
     * Поддерживает ли репозиторий синхронизацию.
     */
    val syncable : Boolean

    suspend fun getAttributes(
        sync : Boolean = true,
        vararg keys : List<ScheduleIdentifier>
    ) : Result<List<LectureAttribute>>
    suspend fun getAttributesOfType(
        type : ScheduleType,
        sync : Boolean = true
    ) : Result<List<LectureAttribute>>
}