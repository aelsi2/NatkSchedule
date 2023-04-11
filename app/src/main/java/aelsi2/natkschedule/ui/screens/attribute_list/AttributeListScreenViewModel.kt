package aelsi2.natkschedule.ui.screens.attribute_list

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.domain.ScreenState
import aelsi2.natkschedule.domain.use_cases.LoadAttributesUseCase
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class AttributeListScreenViewModel(
    scheduleType: ScheduleType,
    private val savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAttributesUseCase
) : ViewModel() {
    private val update = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val searchQuery = savedStateHandle.getStateFlow<String>(FILTER_SEARCH_QUERY_KEY, "")

    private val rawAttributes = MutableStateFlow<List<ScheduleAttribute>>(listOf())
    private lateinit var _attributes: StateFlow<List<ScheduleAttribute>>
    val attributes: StateFlow<List<ScheduleAttribute>>
        get() = _attributes

    val state: StateFlow<ScreenState> = combineTransform(
        networkMonitor.isOnline,
        update
    ) { isOnline, _ ->
        if (!isOnline) {
            emit(ScreenState.NoInternet)
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

    fun update() {
        viewModelScope.launch {
            update.emit(Unit)
        }
    }

    fun setSearchQuery(string: String) {
        savedStateHandle[FILTER_SEARCH_QUERY_KEY] = string
    }

    fun clearSearchQuery() {
        savedStateHandle[FILTER_SEARCH_QUERY_KEY] = ""
    }

    protected fun onPostInit() {
        _attributes = rawAttributes.filterAttributes().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), listOf()
        )
    }

    protected open fun Flow<List<ScheduleAttribute>>.filterAttributes(): Flow<List<ScheduleAttribute>> =
        combine(searchQuery) { attributes, searchQuery ->
            attributes.filter {
                it.matchesString(searchQuery)
            }
        }

    companion object {
        private const val FILTER_SEARCH_QUERY_KEY = "searchQuery"
    }
}