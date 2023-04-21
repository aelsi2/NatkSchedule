package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetScheduleIsFavoriteUseCase(
    private val favoritesReader: FavoritesReader
) {
    operator fun invoke(scheduleIdentifier: Flow<ScheduleIdentifier?>): Flow<Boolean> =
        combine(scheduleIdentifier, favoritesReader.favoriteScheduleIds) { scheduleId, favorites ->
            if (scheduleId == null) {
                false
            } else {
                favorites.contains(scheduleId)
            }
        }
}