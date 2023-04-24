package aelsi2.natkschedule.ui.screens.attribute_list.favorites

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.data.preferences.FavoritesReader
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.domain.use_cases.GetFavoritesNotEmptyUseCase
import aelsi2.natkschedule.domain.use_cases.LoadAttributesUseCase
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.screens.attribute_list.AttributeListScreenViewModel
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class FavoriteListScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    networkMonitor: NetworkMonitor,
    loadAttributes: LoadAttributesUseCase,
    favoritesReader: FavoritesReader,
    getFavoritesNotEmpty: GetFavoritesNotEmptyUseCase,
) : AttributeListScreenViewModel(savedStateHandle) {
    val isNotEmpty: StateFlow<Boolean> = getFavoritesNotEmpty().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    private val rawAttributes = MutableStateFlow<List<ScheduleAttribute>>(emptyList())

    val selectedScheduleType: StateFlow<ScheduleType?> = savedStateHandle.getStateFlow(
        FILTER_SCHEDULE_TYPE_KEY, null
    )

    override val hasFiltersSet: StateFlow<Boolean> = searchString.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    override val attributes: StateFlow<List<ScheduleAttribute>> =
        rawAttributes.applyFilters().applySearch().stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

    override val state: StateFlow<ScreenState> =
        combineTransform(
            networkMonitor.isOnline,
            favoritesReader.favoriteScheduleIds,
            refreshTrigger
        ) { isOnline, favorites, _ ->
            var hadErrors = false
            emit(ScreenState.Loading)
            loadAttributes(
                favorites,
                useLocalRepo = true,
                useNetworkRepo = isOnline,
                onSuccess = { rawAttributes.emit(it) },
                onFailure = { hadErrors = true },
            )
            emit(when {
                hadErrors -> ScreenState.Error
                else -> ScreenState.Loaded
            })
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), ScreenState.Loading
        )

    fun selectScheduleType(scheduleType: ScheduleType?) {
        savedStateHandle[FILTER_SCHEDULE_TYPE_KEY] = scheduleType
    }

    private fun Flow<List<ScheduleAttribute>>.applyFilters(): Flow<List<ScheduleAttribute>> =
        combine(selectedScheduleType) { attributes, type ->
            if (type == null) {
                attributes
            } else {
                attributes.filter {
                    it.scheduleIdentifier.type == type
                }
            }
        }

    init {
        refresh()
    }

    companion object {
        private const val FILTER_SCHEDULE_TYPE_KEY = "scheduleType"
    }
}