package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.model.ScheduleIdentifier
import kotlinx.coroutines.flow.Flow

interface GetScheduleParametersUseCase {
    operator fun invoke(): Flow<ScheduleParameters>
}

data class ScheduleParameters(
    val identifier: ScheduleIdentifier?,
    val cache: Boolean
)