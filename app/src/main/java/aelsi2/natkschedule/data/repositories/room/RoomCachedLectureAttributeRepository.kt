package aelsi2.natkschedule.data.repositories.room

import aelsi2.natkschedule.data.repositories.LectureAttributeRepository
import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType

class RoomCachedLectureAttributeRepository(

) : LectureAttributeRepository {
    override val syncable: Boolean
        get() = true

    override suspend fun getAttributes(
        keys: List<ScheduleIdentifier>,
        sync: Boolean
    ): Result<List<LectureAttribute>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAttributesOfType(
        type: ScheduleType,
        sync: Boolean
    ): Result<List<LectureAttribute>> {
        TODO("Not yet implemented")
    }
}