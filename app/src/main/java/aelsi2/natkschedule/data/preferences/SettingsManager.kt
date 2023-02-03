package aelsi2.natkschedule.data.preferences

interface SettingsManager : SettingsReader {
    suspend fun setCacheMainScheduleEnabled(value : Boolean)
    suspend fun setCacheFavoriteSchedulesEnabled(value : Boolean)
    suspend fun setCacheInBackgroundEnabled(value : Boolean)
    suspend fun setBackgroundCachingIntervalSeconds(value : Long)
    suspend fun resetAll()
}