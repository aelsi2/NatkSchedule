package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.data.preferences.SettingsReader
import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleDayRepository
import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.first

class CleanCacheUnusedUseCase(
    private val localLectures: WritableScheduleDayRepository,
    private val localAttributes: WritableScheduleAttributeRepository,
    private val settings: SettingsReader,
    private val favorites: FavoritesReader,
    private val timeManager: TimeManager
) {
    suspend operator fun invoke() {
        cleanUnusedSchedules()
        cleanUnusedAttributes()
    }

    private suspend fun cleanLectures() {
        val cleanCacheTimeDays = settings.keepLecturesForDays.first()
        localLectures.deleteAllBefore(
            timeManager.currentCollegeLocalDateTime.minusDays(
                cleanCacheTimeDays.toLong()
            ).toLocalDate()
        )
    }

    private suspend fun cleanUnusedAttributes() {
        val favoriteSchedules = favorites.favoriteScheduleIds.first()
        val mainSchedule = favorites.mainScheduleId.first()
        val attributesToKeep = ArrayList(favoriteSchedules)
        if (mainSchedule != null) {
            attributesToKeep.add(mainSchedule)
        }
        localAttributes.deleteUnused(attributesToKeep)
    }

    private suspend fun cleanUnusedSchedules() {
        val cacheMainEnabled = settings.cacheMainScheduleEnabled.first()
        val cacheFavoritesEnabled = settings.cacheFavoriteSchedulesEnabled.first()
        val schedulesToKeep = ArrayList<ScheduleIdentifier>()
        if (cacheMainEnabled) {
            val mainSchedule = favorites.mainScheduleId.first()
            if (mainSchedule != null) {
                schedulesToKeep.add(mainSchedule)
            }
        }
        if (cacheFavoritesEnabled) {
            val favorites = favorites.favoriteScheduleIds.first()
            schedulesToKeep.addAll(favorites)
        }
        localLectures.deleteAllExcept(schedulesToKeep)
    }
}