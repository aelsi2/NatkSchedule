package aelsi2.natkschedule.data.repositories.network

import aelsi2.natkschedule.data.repositories.ScheduleRepository
import aelsi2.natkschedule.model.*
import java.time.Instant

class NetworkOnlyScheduleRepository : ScheduleRepository {
    override val syncable: Boolean
        get() = false
    override suspend fun getSchedule(
        startDate: Instant,
        endDate: Instant,
        identifier : ScheduleIdentifier,
        sync : Boolean
    ) : Result<Iterable<Lecture>> {
        TODO("Not yet implemented")
    }

}