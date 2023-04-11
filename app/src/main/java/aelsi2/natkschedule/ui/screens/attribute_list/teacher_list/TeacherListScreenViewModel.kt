package aelsi2.natkschedule.ui.screens.attribute_list.teacher_list

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.domain.use_cases.LoadAttributesUseCase
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.screens.attribute_list.AttributeListScreenViewModel
import androidx.lifecycle.SavedStateHandle

class TeacherListScreenViewModel(
    savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAttributesUseCase
) : AttributeListScreenViewModel(
    ScheduleType.TEACHER,
    savedStateHandle,
    networkMonitor,
    loadAttributes
) {

    init {
        onPostInit()
        update()
    }
}