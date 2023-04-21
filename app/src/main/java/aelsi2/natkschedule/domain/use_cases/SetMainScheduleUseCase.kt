package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesManager
import aelsi2.natkschedule.model.ScheduleIdentifier

class SetMainScheduleUseCase(
    private val favoritesManager: FavoritesManager
) {
    suspend operator fun invoke(scheduleIdentifier: ScheduleIdentifier?) {
        favoritesManager.setMainScheduleId(scheduleIdentifier)
    }
}