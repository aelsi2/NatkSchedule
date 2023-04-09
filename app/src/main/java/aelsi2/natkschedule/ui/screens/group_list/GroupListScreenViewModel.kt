package aelsi2.natkschedule.ui.screens.group_list

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.data.repositories.ScheduleAttributeRepository
import aelsi2.natkschedule.domain.ScreenState
import aelsi2.natkschedule.model.Group
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupListScreenViewModel(
    scheduleAttributeRepository: ScheduleAttributeRepository,
    networkMonitor: NetworkMonitor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val attributesUnfiltered = MutableStateFlow<List<ScheduleAttribute>>(listOf())
    private val update = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        update()
    }

    val searchQuery = savedStateHandle.getStateFlow<String>(FILTER_SEARCH_QUERY_KEY, "")

    val attributes: StateFlow<List<ScheduleAttribute>> = combine(
        attributesUnfiltered,
        searchQuery,
        update
    ) {attributes, searchQuery, _ ->
        val groups = ArrayList<Group>()
        attributes.forEach {
            if (it is Group && (searchQuery.isBlank() || it.name.contains(searchQuery, true))){
                groups.add(it)
            }
        }
        groups.sortedWith(
            compareBy{ it.name}
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    val state: StateFlow<ScreenState> = combineTransform(
        networkMonitor.isOnline,
        update
    ) {isOnline, _ ->
        if (!isOnline) {
            emit(ScreenState.NoInternet)
            return@combineTransform
        }
        emit(ScreenState.Loading)
        val result = scheduleAttributeRepository.getAllAttributes(ScheduleType.GROUP)
        result.fold(
            onSuccess = {
                attributesUnfiltered.emit(it)
                emit(ScreenState.Loaded)
            },
            onFailure = {
                emit(ScreenState.Error)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading)

    fun update() {
        viewModelScope.launch {
            update.emit(Unit)
        }
    }

    fun setSearchQuery(query: String) {
        savedStateHandle[FILTER_SEARCH_QUERY_KEY] = query
    }

    companion object {
        private const val FILTER_SEARCH_QUERY_KEY = "searchQuery"
        private const val FILTER_PROGRAM_KEY = "program"
        private const val FILTER_YEAR_KEY = "year"
    }
}