package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.FavoritesReader
import kotlinx.coroutines.flow.map

class GetFavoritesNotEmptyUseCase(
    private val favoritesReader: FavoritesReader
) {
    operator fun invoke() = favoritesReader.favoriteScheduleIds.map {
        it.isNotEmpty()
    }
}