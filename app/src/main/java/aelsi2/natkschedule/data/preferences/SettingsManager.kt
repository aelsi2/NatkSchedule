package aelsi2.natkschedule.data.preferences

interface SettingsManager : SettingsReader {
    suspend fun setSaveMainScheduleEnabled(value : Boolean)
    suspend fun setSaveFavoritesEnabled(value : Boolean)
    suspend fun setBackgroundSyncEnabled(value : Boolean)
    suspend fun setBackgroundSyncIntervalHours(value : Int)
    suspend fun setCleanOldSchedulesEnabled(value: Boolean)
    suspend fun setSavedScheduleMaxAgeDays(value : Int)
    suspend fun resetAll()
}