package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.repositories.WritableScheduleDayRepository

class ClearSavedSchedulesUseCase(
    private val localDays: WritableScheduleDayRepository,
) {
    suspend operator fun invoke() {
        localDays.deleteAll()
    }
}