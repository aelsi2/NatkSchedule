package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesManager
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.first

class ToggleScheduleFavoriteUseCase(
    private val favoritesManager: FavoritesManager
) {
    suspend operator fun invoke(scheduleIdentifier: ScheduleIdentifier) {
        if (favoritesManager.isInFavorites(scheduleIdentifier).first()) {
            favoritesManager.removeFromFavorites(scheduleIdentifier)
        }
        else {
            favoritesManager.addToFavorites(scheduleIdentifier)
        }
    }
}