package aelsi2.natkschedule.data.preferences

import aelsi2.natkschedule.model.ScheduleIdentifier

interface FavoritesManager : FavoritesReader {
    suspend fun addToFavorites(schedule : ScheduleIdentifier)
    suspend fun removeFromFavorites(schedule : ScheduleIdentifier)
    suspend fun clearFavorites()
    suspend fun setMainScheduleId(value : ScheduleIdentifier?)
}