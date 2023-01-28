package aelsi2.natkschedule.data.repositories

import kotlinx.coroutines.flow.Flow

interface ScheduleMetaRepository<T> {
    suspend fun getItems() : Flow<Sequence<T>>
    suspend fun syncAndGetItems() : Flow<Sequence<T>>
}