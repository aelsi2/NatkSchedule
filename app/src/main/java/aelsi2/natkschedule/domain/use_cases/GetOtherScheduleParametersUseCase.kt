package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.data.preferences.SettingsReader
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.*

class GetOtherScheduleParametersUseCase(
    private val identifier: ScheduleIdentifier,
    private val favoritesReader: FavoritesReader,
    private val settingsReader: SettingsReader,
) : GetScheduleParametersUseCase {
    override fun invoke(): Flow<ScheduleParameters> = combine(
        favoritesReader.isInFavorites(identifier),
        favoritesReader.mainScheduleId.map { it == identifier },
        settingsReader.cacheFavoriteSchedulesEnabled,
        settingsReader.cacheMainScheduleEnabled
    ) { isFavorite, isMain, cacheFavorite, cacheMain ->
        when {
            isMain -> ScheduleParameters(identifier, cacheMain)
            isFavorite -> ScheduleParameters(identifier, cacheFavorite)
            else -> ScheduleParameters(identifier, false)
        }
    }.conflate().distinctUntilChanged()
}