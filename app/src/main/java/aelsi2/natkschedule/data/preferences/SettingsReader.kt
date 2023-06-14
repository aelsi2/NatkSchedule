package aelsi2.natkschedule.data.preferences

import kotlinx.coroutines.flow.Flow

interface SettingsReader {
    val saveMainScheduleEnabled : Flow<Boolean>
    val saveFavoritesEnabled : Flow<Boolean>
    val backgroundSyncEnabled : Flow<Boolean>
    val backgroundSyncIntervalHours : Flow<Int>
    val cleanCacheOnStartupEnabled : Flow<Boolean>
    val cleanCacheOnSyncEnabled : Flow<Boolean>
    val keepScheduleForDays : Flow<Int>
}