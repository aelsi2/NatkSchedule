package aelsi2.natkschedule.ui.screens.attribute_list.teachers

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.domain.use_cases.LoadAllAttributesUseCase
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.screens.attribute_list.OnlineAttributeListScreenViewModel
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class TeacherListScreenViewModel(
    savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAllAttributesUseCase
) : OnlineAttributeListScreenViewModel(
    ScheduleType.Teacher,
    savedStateHandle,
    networkMonitor,
    loadAttributes
) {
    override val attributes: StateFlow<List<ScheduleAttribute>> =
        rawAttributes.applySearch().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), listOf()
        )
    override val hasFiltersSet: StateFlow<Boolean> = searchString.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}