package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.data.repositories.WritableScheduleAttributeRepository
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType

class LoadAttributesUseCase(
    private val networkAttributeRepo: ScheduleAttributeRepository
) {
    suspend operator fun invoke(
        type: ScheduleType,
        onSuccess: (suspend (List<ScheduleAttribute>) -> Unit)?,
        onFailure: (suspend (Throwable) -> Unit)?
    ) {
        networkAttributeRepo.getAllAttributes(type).fold(
            onSuccess = {
                onSuccess?.invoke(it)
            },
            onFailure = {
                onFailure?.invoke(it)
            }
        )
    }
}