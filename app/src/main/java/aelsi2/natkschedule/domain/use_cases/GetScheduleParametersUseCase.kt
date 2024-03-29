package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.data.preferences.SettingsReader
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class GetMainScheduleParametersUseCase(
    private val favorites: FavoritesReader,
    private val settings: SettingsReader
) : GetScheduleParametersUseCase {
    override fun invoke(): Flow<ScheduleParameters> = favorites.mainScheduleId.combine(settings.saveMainScheduleEnabled) { identifier, cache ->
        ScheduleParameters(identifier, cache)
    }.conflate().distinctUntilChanged()
}

class GetRegularScheduleParametersUseCase(
    private val identifier: ScheduleIdentifier,
    private val favoritesReader: FavoritesReader,
    private val settingsReader: SettingsReader,
) : GetScheduleParametersUseCase {
    override fun invoke(): Flow<ScheduleParameters> = combine(
        favoritesReader.isInFavorites(identifier),
        favoritesReader.mainScheduleId.map { it == identifier },
        settingsReader.saveFavoritesEnabled,
        settingsReader.saveMainScheduleEnabled
    ) { isFavorite, isMain, cacheFavorite, cacheMain ->
        when {
            isMain -> ScheduleParameters(identifier, cacheMain)
            isFavorite -> ScheduleParameters(identifier, cacheFavorite)
            else -> ScheduleParameters(identifier, false)
        }
    }.conflate().distinctUntilChanged()
}

interface GetScheduleParametersUseCase {
    operator fun invoke(): Flow<ScheduleParameters>
}

data class ScheduleParameters(
    val identifier: ScheduleIdentifier?,
    val cache: Boolean
)