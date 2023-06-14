package aelsi2.natkschedule.data.preferences

interface SettingsManager : SettingsReader {
    suspend fun setSaveMainScheduleEnabled(value : Boolean)
    suspend fun setSaveFavoritesEnabled(value : Boolean)
    suspend fun setCacheBackgroundSyncEnabled(value : Boolean)
    suspend fun setBackgroundSyncIntervalHours(value : Int)
    suspend fun setCleanCacheOnStartupEnabled(value: Boolean)
    suspend fun setCleanCacheOnSyncEnabled(value: Boolean)
    suspend fun setKeepScheduleForDays(value : Int)
    suspend fun resetAll()
}