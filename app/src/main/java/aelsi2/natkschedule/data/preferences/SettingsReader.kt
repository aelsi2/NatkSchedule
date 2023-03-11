package aelsi2.natkschedule.data.preferences

import kotlinx.coroutines.flow.Flow

interface SettingsReader {
    val cacheMainScheduleEnabled : Flow<Boolean>
    val cacheFavoriteSchedulesEnabled : Flow<Boolean>
    val cacheInBackgroundEnabled : Flow<Boolean>
    val backgroundCachingIntervalSeconds : Flow<Long>
    val cleanLectureCacheAutomatically : Flow<Boolean>
    val keepLecturesForDays : Flow<Int>
}