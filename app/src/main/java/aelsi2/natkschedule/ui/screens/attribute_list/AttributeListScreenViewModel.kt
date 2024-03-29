package aelsi2.natkschedule.ui.screens.attribute_list

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.domain.use_cases.LoadAllAttributesUseCase
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType
import androidx.annotation.CallSuper
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Stable
abstract class OnlineAttributeListScreenViewModel(
    scheduleType: ScheduleType,
    savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAllAttributesUseCase
) : AttributeListScreenViewModel(savedStateHandle) {
    protected val rawAttributes = MutableStateFlow<List<ScheduleAttribute>>(listOf())

    override val state: StateFlow<ScreenState> = combineTransform(
        networkMonitor.isOnline,
        refreshTrigger
    ) { isOnline, _ ->
        if (!isOnline) {
            emit(ScreenState.Loaded)
            return@combineTransform
        }
        emit(ScreenState.Loading)
        loadAttributes(
            scheduleType,
            onSuccess = {
                rawAttributes.emit(it)
                emit(ScreenState.Loaded)
            },
            onFailure = {
                emit(ScreenState.Error)
            }
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
    )

    init {
        refresh()
    }
}

@Stable
abstract class AttributeListScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    protected val refreshTrigger: MutableSharedFlow<Unit> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val searchString: StateFlow<String> = savedStateHandle.getStateFlow(
        FILTER_SEARCH_STRING_KEY, ""
    )
    abstract val attributes: StateFlow<List<ScheduleAttribute>>
    abstract val state: StateFlow<ScreenState>

    abstract val hasFiltersSet: StateFlow<Boolean>

    fun refresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    fun setSearchString(string: String) {
        savedStateHandle[FILTER_SEARCH_STRING_KEY] = string
    }

    fun resetSearchString() {
        setSearchString("")
    }

    @CallSuper
    open fun resetFilters() {
        resetSearchString()
    }

    protected fun Flow<List<ScheduleAttribute>>.applySearch(): Flow<List<ScheduleAttribute>> =
        combine(searchString) { attributes, search ->
            if (search.isBlank()) {
                attributes
            } else {
                attributes.filter {
                    it.matchesString(search)
                }
            }
        }

    companion object {
        private const val FILTER_SEARCH_STRING_KEY = "searchString"
    }
}