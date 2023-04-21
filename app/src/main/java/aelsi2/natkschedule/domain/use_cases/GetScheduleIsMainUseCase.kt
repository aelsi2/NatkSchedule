package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetScheduleIsMainUseCase(
    private val favoritesReader: FavoritesReader
) {
    operator fun invoke(scheduleIdentifier: Flow<ScheduleIdentifier?>): Flow<Boolean> =
        combine(scheduleIdentifier, favoritesReader.mainScheduleId) {scheduleId, mainScheduleId ->
            mainScheduleId == scheduleId
        }
}