package aelsi2.natkschedule.data.preferences

import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.Flow

interface FavoritesReader {
    val mainScheduleId : Flow<ScheduleIdentifier?>
    val favoriteScheduleIds : Flow<List<ScheduleIdentifier>>
    fun isInFavorites(scheduleId : ScheduleIdentifier) : Flow<Boolean>
}