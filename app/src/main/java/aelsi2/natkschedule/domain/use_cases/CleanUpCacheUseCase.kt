package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.data.preferences.SettingsReader
import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleDayRepository
import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.first

class CleanUpCacheUseCase(
    private val localDays: WritableScheduleDayRepository,
    private val localAttributes: WritableScheduleAttributeRepository,
    private val settings: SettingsReader,
    private val favorites: FavoritesReader,
    private val timeManager: TimeManager
) {
    suspend operator fun invoke() {
        cleanUnusedDays()
        cleanOldDays()
        cleanUnusedAttributes()
    }

    private suspend fun cleanOldDays() {
        val cleanCacheTimeDays = settings.keepScheduleForDays.first()

        localDays.deleteAllBefore(
            timeManager.currentCollegeLocalDateTime.minusDays(
                cleanCacheTimeDays.toLong()
            ).toLocalDate()
        )
    }

    private suspend fun cleanUnusedAttributes() {
        val favoriteSchedules = favorites.favoriteScheduleIds.first()
        val attributesToKeep = ArrayList(favoriteSchedules)

        val mainSchedule = favorites.mainScheduleId.first()
        if (mainSchedule != null) {
            attributesToKeep.add(mainSchedule)
        }

        localAttributes.deleteUnusedExcept(attributesToKeep)
    }

    private suspend fun cleanUnusedDays() {
        val schedulesToKeep = ArrayList<ScheduleIdentifier>()

        val cacheMainEnabled = settings.saveMainScheduleEnabled.first()
        if (cacheMainEnabled) {
            val mainSchedule = favorites.mainScheduleId.first()
            if (mainSchedule != null) {
                schedulesToKeep.add(mainSchedule)
            }
        }
        val cacheFavoritesEnabled = settings.saveFavoritesEnabled.first()
        if (cacheFavoritesEnabled) {
            val favorites = favorites.favoriteScheduleIds.first()
            schedulesToKeep.addAll(favorites)
        }

        localDays.deleteAllExcept(schedulesToKeep)
    }
}