package aelsi2.natkschedule.domain

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.data.preferences.SettingsReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

class GetMainScheduleParametersUseCase(
    private val favorites: FavoritesReader,
    private val settings: SettingsReader
) : GetScheduleParametersUseCase {
    override fun invoke(): Flow<ScheduleParameters> = favorites.mainScheduleId.combine(settings.cacheMainScheduleEnabled) { identifier, cache ->
        ScheduleParameters(identifier, cache)
    }.conflate().distinctUntilChanged()
}