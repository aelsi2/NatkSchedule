package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class LoadAttributesUseCase(
    private val localAttributeRepo: WritableScheduleAttributeRepository,
    private val networkAttributeRepo: ScheduleAttributeRepository
) {
    suspend operator fun invoke(
        attributes: List<ScheduleIdentifier>,
        loadOffline: Boolean,
        loadOnline: Boolean,
        storeOffline: Boolean,
        onOfflineSuccess: (suspend (List<ScheduleAttribute>) -> Unit) = {},
        onOfflineError: (suspend (Throwable) -> Unit) = {},
        onOnlineSuccess: (suspend (List<ScheduleAttribute>) -> Unit) = {},
        onOnlineError: (suspend (Throwable) -> Unit) = {},
        onOfflineStoreSuccess: (suspend (List<ScheduleAttribute>) -> Unit) = {},
        onOfflineStoreError: (suspend (Throwable) -> Unit) = {},
    ) = coroutineScope {
        val offlineJob = if (loadOffline) async {
            this@LoadAttributesUseCase.localAttributeRepo.getAttributesById(attributes)
        } else null
        val onlineJob = if (loadOnline) async {
            this@LoadAttributesUseCase.networkAttributeRepo.getAttributesById(attributes)
        } else null
        offlineJob?.await()?.fold(
            onSuccess = {
                onOfflineSuccess(it)
            },
            onFailure = {
                onOfflineError(it)
            }
        )
        var offlineStoreJob: Job? = null
        onlineJob?.await()?.fold(
            onSuccess = {
                if (storeOffline) {
                    offlineStoreJob = async {
                        this@LoadAttributesUseCase.localAttributeRepo.putAttributes(it)
                    }
                }
                onOnlineSuccess(it)
            },
            onFailure = {
                onOnlineError(it)
            }
        )
        if (offlineStoreJob != null) {
            offlineStoreJob!!.join()
            if (loadOffline) {
                this@LoadAttributesUseCase.localAttributeRepo.getAttributesById(attributes).fold(
                    onSuccess = {
                        onOfflineStoreSuccess(it)
                    },
                    onFailure = {
                        onOfflineStoreError(it)
                    }
                )
            }
        }
    }
}