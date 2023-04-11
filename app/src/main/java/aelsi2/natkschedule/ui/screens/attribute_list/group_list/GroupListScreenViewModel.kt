package aelsi2.natkschedule.ui.screens.attribute_list.group_list

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.domain.use_cases.LoadAttributesUseCase
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.screens.attribute_list.AttributeListScreenViewModel
import androidx.lifecycle.SavedStateHandle

class GroupListScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAttributesUseCase
) : AttributeListScreenViewModel(
    ScheduleType.GROUP,
    savedStateHandle,
    networkMonitor,
    loadAttributes
) {

    init {
        onPostInit()
        update()
    }

    companion object {
        private const val FILTER_PROGRAM_KEY = "program"
        private const val FILTER_YEAR_KEY = "year"
    }
}