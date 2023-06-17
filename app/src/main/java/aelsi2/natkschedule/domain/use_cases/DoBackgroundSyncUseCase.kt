package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.data.preferences.SettingsReader
import aelsi2.natkschedule.data.time.TimeManager
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class DoBackgroundSyncUseCase(
    private val cleanUpCache: CleanCacheUseCase,
    private val loadSchedule: LoadScheduleUseCase,
    private val settingsReader: SettingsReader,
    private val favoritesReader: FavoritesReader,
    private val timeManager: TimeManager,
) {
    suspend operator fun invoke() {
        if (settingsReader.cleanOldSchedulesEnabled.first()) {
            cleanUpCache()
        }
        if (settingsReader.backgroundSyncEnabled.first()) {
            val startDate = timeManager.collegeLocalDate.first()
            val endDate = startDate.plusDays(CACHE_AHEAD_DAYS)
            if (settingsReader.saveMainScheduleEnabled.first()) {
                cacheMainSchedule(startDate, endDate)
            }
            if (settingsReader.saveFavoritesEnabled.first()) {
                cacheFavoriteSchedules(startDate, endDate)
            }
        }
    }
    private suspend fun cacheMainSchedule(startDate: LocalDate, endDate: LocalDate) {
        val mainSchedule = favoritesReader.mainScheduleId.first()
        if (mainSchedule != null) {
            loadSchedule(
                identifier = mainSchedule,
                startDate = startDate,
                endDate = endDate,
                useNetworkRepo = true,
                useLocalRepo = true
            )
        }
    }
    private suspend fun cacheFavoriteSchedules(startDate: LocalDate, endDate: LocalDate) {
        val favorites = favoritesReader.favoriteScheduleIds.first()
        for (schedule in favorites) {
            loadSchedule(
                identifier = schedule,
                startDate = startDate,
                endDate = endDate,
                useNetworkRepo = true,
                useLocalRepo = true
            )
        }
    }

    companion object {
        private const val CACHE_AHEAD_DAYS: Long = 6
    }
}