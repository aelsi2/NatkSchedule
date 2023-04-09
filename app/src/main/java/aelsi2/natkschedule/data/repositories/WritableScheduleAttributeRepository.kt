package aelsi2.natkschedule.data.repositories

import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier

interface WritableScheduleAttributeRepository : ScheduleAttributeRepository {
    suspend fun putAttributes(attributes: List<ScheduleAttribute>)
    suspend fun putAttribute(attribute: ScheduleAttribute)
    suspend fun deleteUnused(attributesToKeep : List<ScheduleIdentifier>)
}