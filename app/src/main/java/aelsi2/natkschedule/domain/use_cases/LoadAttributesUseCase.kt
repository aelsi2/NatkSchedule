package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class LoadAttributesUseCase(
    private val localAttributeRepo: WritableScheduleAttributeRepository,
    private val networkAttributeRepo: ScheduleAttributeRepository
) {
    suspend operator fun invoke(
        attributeIds: List<ScheduleIdentifier>,
        useLocalRepo: Boolean,
        useNetworkRepo: Boolean,
        onSuccess: (suspend (List<ScheduleAttribute>) -> Unit) = {},
        onFailure: (suspend (Throwable) -> Unit) = {},
    ) = coroutineScope {
        val offlineJob = if (useLocalRepo) async {
            this@LoadAttributesUseCase.localAttributeRepo.getAttributesById(attributeIds)
        } else null
        val onlineJob = if (useNetworkRepo) async {
            this@LoadAttributesUseCase.networkAttributeRepo.getAttributesById(attributeIds)
        } else null
        offlineJob?.await()?.fold(
            onSuccess = {
                onSuccess(it)
            },
            onFailure = {
                onFailure(it)
            }
        )
        var onlineAttributes: List<ScheduleAttribute>? = null
        onlineJob?.await()?.fold(
            onSuccess = {
                onlineAttributes = it
            },
            onFailure = {
                onFailure(it)
            }
        )
        if (!useLocalRepo) {
            if (onlineAttributes != null) {
                onSuccess(onlineAttributes!!)
            }
            return@coroutineScope
        }
        val storeJob = if (onlineAttributes != null) async {
            localAttributeRepo.putAttributes(onlineAttributes!!)
            localAttributeRepo.getAttributesById(attributeIds)
        } else null
        storeJob?.await()?.fold(
            onSuccess = {
                onSuccess(it)
            },
            onFailure = {
                onFailure(it)
            }
        )
    }
}