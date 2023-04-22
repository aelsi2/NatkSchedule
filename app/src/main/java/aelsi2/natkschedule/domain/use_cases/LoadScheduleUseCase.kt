package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.ScheduleDayRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleDayRepository
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate

class LoadScheduleUseCase(
    private val networkDayRepo: ScheduleDayRepository,
    private val localDayRepo: WritableScheduleDayRepository,
    private val networkAttributeRepo: ScheduleAttributeRepository,
    private val localAttributeRepo: WritableScheduleAttributeRepository
) {
    suspend operator fun invoke(
        identifier: ScheduleIdentifier,
        loadOffline: Boolean,
        loadOnline: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        onOfflineDaysSuccess: (suspend (List<ScheduleDay>) -> Unit) = {},
        onOfflineAttributeSuccess: (suspend (ScheduleAttribute) -> Unit) = {},
        onOfflineDaysError: (suspend (Throwable) -> Unit) = {},
        onOfflineAttributeError: (suspend (Throwable) -> Unit) = {},
        onOnlineDaysSuccess: (suspend (List<ScheduleDay>) -> Unit) = {},
        onOnlineAttributeSuccess: (suspend (ScheduleAttribute) -> Unit) = {},
        onOnlineDaysError: (suspend (Throwable) -> Unit) = {},
        onOnlineAttributeError: (suspend (Throwable) -> Unit) = {},
    ) = coroutineScope {
        val offlineDaysJob = if (loadOffline) async {
            this@LoadScheduleUseCase.localDayRepo.getDays(startDate, endDate, identifier)
        } else null
        val offlineAttributeJob = if (loadOffline) async {
            this@LoadScheduleUseCase.localAttributeRepo.getAttributeById(identifier)
        } else null
        val onlineDaysJob = if (loadOnline) async {
            this@LoadScheduleUseCase.networkDayRepo.getDays(startDate, endDate, identifier)
        } else null
        val onlineAttributeJob = if (loadOnline) async {
            this@LoadScheduleUseCase.networkAttributeRepo.getAttributeById(identifier)
        } else null

        offlineDaysJob?.await()?.fold(
            onSuccess = {
                onOfflineDaysSuccess(it)
            },
            onFailure = {
                onOfflineDaysError(it)
            }
        )
        offlineAttributeJob?.await()?.fold(
            onSuccess = {
                onOfflineAttributeSuccess(it)
            },
            onFailure = {
                onOfflineAttributeError(it)
            }
        )
        onlineDaysJob?.await()?.fold(
            onSuccess = {
                localDayRepo.putDays(identifier, it)
                onOnlineDaysSuccess(it)
            },
            onFailure = {
                onOnlineDaysError(it)
            }
        )
        onlineAttributeJob?.await()?.fold(
            onSuccess = {
                localAttributeRepo.putAttribute(it)
                onOnlineAttributeSuccess(it)
            },
            onFailure = {
                onOnlineAttributeError(it)
            }
        )
    }
}