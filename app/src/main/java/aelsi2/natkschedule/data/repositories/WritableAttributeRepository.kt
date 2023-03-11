package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier

interface WritableAttributeRepository : AttributeRepository {
    suspend fun putAttributes(attributes: List<LectureAttribute>)
    suspend fun deleteUnused(attributesToKeep : List<ScheduleIdentifier>)
}