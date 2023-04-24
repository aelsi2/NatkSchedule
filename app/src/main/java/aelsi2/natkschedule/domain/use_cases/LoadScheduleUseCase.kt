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
        startDate: LocalDate,
        endDate: LocalDate,
        useNetworkRepo: Boolean,
        useLocalRepo: Boolean,
        onSuccess: (suspend (ScheduleAttribute?, List<ScheduleDay>?) -> Unit) = {_, _ ->},
        onFailure: (suspend (Throwable) -> Unit) = {},
    ) = coroutineScope {
        val offlineDaysJob = if (useLocalRepo) async {
            localDayRepo.getDays(startDate, endDate, identifier)
        } else null
        val offlineAttributeJob = if (useLocalRepo) async {
            localAttributeRepo.getAttributeById(identifier)
        } else null
        val onlineDaysJob = if (useNetworkRepo) async {
            networkDayRepo.getDays(startDate, endDate, identifier)
        } else null
        val onlineAttributeJob = if (useNetworkRepo) async {
            networkAttributeRepo.getAttributeById(identifier)
        } else null

        var offlineDays: List<ScheduleDay>? = null
        offlineDaysJob?.await()?.fold(
            onSuccess = {
                offlineDays = it
            },
            onFailure = {
                onFailure(it)
            }
        )
        var offlineAttribute: ScheduleAttribute? = null
        offlineAttributeJob?.await()?.fold(
            onSuccess = {
                offlineAttribute = it
            },
            onFailure = {
                onFailure(it)
            }
        )
        if (offlineDays != null || offlineAttribute != null) {
            onSuccess(offlineAttribute, offlineDays)
        }

        var onlineDays: List<ScheduleDay>? = null
        onlineDaysJob?.await()?.fold(
            onSuccess = {
                onlineDays = it
            },
            onFailure = {
                onFailure(it)
            }
        )
        var onlineAttribute: ScheduleAttribute? = null
        onlineAttributeJob?.await()?.fold(
            onSuccess = {
                onlineAttribute = it
            },
            onFailure = {
                onFailure(it)
            }
        )
        if (!useLocalRepo) {
            if (onlineAttribute != null || onlineDays != null) {
                onSuccess(onlineAttribute, onlineDays)
            }
            return@coroutineScope
        }
        val storeDaysJob = if (onlineDays != null) async {
            localDayRepo.putDays(identifier, onlineDays!!)
            localDayRepo.getDays(startDate, endDate, identifier)
        } else null
        val storeAttributeJob = if (onlineAttribute != null) async {
            localAttributeRepo.putAttribute(onlineAttribute!!)
            localAttributeRepo.getAttributeById(identifier)
        } else null

        var storedAttribute: ScheduleAttribute? = null
        storeAttributeJob?.await()?.fold(
            onSuccess = {
                storedAttribute = it
            },
            onFailure = {
                onFailure(it)
            }
        )
        var storedDays: List<ScheduleDay>? = null
        storeDaysJob?.await()?.fold(
            onSuccess = {
                storedDays = it
            },
            onFailure = {
                onFailure(it)
            }
        )
        if (storedAttribute != null || storedDays != null) {
            onSuccess(storedAttribute, storedDays)
        }
    }
}