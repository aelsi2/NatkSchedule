package aelsi2.natkschedule.data.preferences

import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.Flow

interface FavoritesManager {
    val mainScheduleId : Flow<ScheduleIdentifier?>
    suspend fun setMainScheduleId(value : ScheduleIdentifier?)
    val favoriteSchedules : Flow<List<ScheduleIdentifier>>
    fun isInFavorites(schedule : ScheduleIdentifier) : Flow<Boolean>
    suspend fun addToFavorites(schedule : ScheduleIdentifier)
    suspend fun removeFromFavorites(schedule : ScheduleIdentifier)
    suspend fun clearFavorites()
}