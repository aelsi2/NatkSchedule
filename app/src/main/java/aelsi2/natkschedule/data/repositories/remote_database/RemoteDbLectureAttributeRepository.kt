package aelsi2.natkschedule.data.repositories.remote_database

import aelsi2.natkschedule.data.repositories.LectureAttributeRepository
import aelsi2.natkschedule.model.LectureAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType

class RemoteDbLectureAttributeRepository(private val databaseManager: RemoteDbManager) : LectureAttributeRepository {
    override val syncable: Boolean
        get() = false

    override suspend fun getAttributes(
        sync: Boolean,
        vararg keys: List<ScheduleIdentifier>
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