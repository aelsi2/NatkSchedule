package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.domain.model.LectureState
import aelsi2.natkschedule.domain.use_cases.*
import aelsi2.natkschedule.model.Lecture
import aelsi2.natkschedule.model.ScheduleAttribute
import aelsi2.natkschedule.model.ScheduleDay
import aelsi2.natkschedule.model.ScheduleIdentifier
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

@Stable
class ScheduleScreenViewModel(
    getScheduleParameters: GetScheduleParametersUseCase,
    getScheduleIsMain: GetScheduleIsMainUseCase,
    getScheduleIsFavorite: GetScheduleIsFavoriteUseCase,
    networkMonitor: NetworkMonitor,
    timeManager: TimeManager,
    private val savedStateHandle: SavedStateHandle,
    private val loadSchedule: LoadScheduleUseCase,
    private val getLectureStateUseCase: GetLectureStateUseCase,
    private val setMainSchedule: SetMainScheduleUseCase,
    private val toggleScheduleFavorite: ToggleScheduleFavoriteUseCase
) : ViewModel() {
    val scheduleIdentifier: StateFlow<ScheduleIdentifier?> = getScheduleParameters().map {
        it.identifier
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )
    val scheduleAttribute: StateFlow<ScheduleAttribute?>
        get() = _scheduleAttribute
    private val _scheduleAttribute = MutableStateFlow<ScheduleAttribute?>(null)

    val days: StateFlow<List<ScheduleDay>>
        get() = _days
    private val _days = MutableStateFlow<List<ScheduleDay>>(listOf())

    private val freeScrollStartDate: StateFlow<LocalDate> = savedStateHandle.getStateFlow(
        START_DATE_KEY, timeManager.currentCollegeLocalDate
    )
    private val freeScrollEndDate: StateFlow<LocalDate> = savedStateHandle.getStateFlow(
        END_DATE_KEY,
        timeManager.currentCollegeLocalDate.plusDays(7)
    )

    val displayMode =
        savedStateHandle.getStateFlow(DISPLAY_MODE_KEY, ScheduleDisplayMode.ONE_WEEK_FROM_TODAY)

    val startDate: StateFlow<LocalDate> = combine(
        displayMode,
        timeManager.collegeLocalDate.conflate(),
        freeScrollStartDate
    ) { displayMode, currentDate, startDate ->
        getStartDate(displayMode, currentDate, startDate)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        getStartDate(
            displayMode.value,
            timeManager.currentCollegeLocalDate,
            freeScrollStartDate.value
        )
    )
    val endDate: StateFlow<LocalDate> = combine(
        displayMode,
        timeManager.collegeLocalDate.conflate(),
        freeScrollEndDate
    ) { displayMode, currentDate, endDate ->
        getEndDate(displayMode, currentDate, endDate)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        getEndDate(
            displayMode.value,
            timeManager.currentCollegeLocalDate,
            freeScrollEndDate.value
        )
    )

    private val refresh = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val state: StateFlow<ScreenState> =
        combineTransform(
            getScheduleParameters(),
            networkMonitor.isOnline,
            refresh.conflate(),
            startDate,
            endDate
        ) { params, isOnline, _, startDate, endDate ->
            if (params.identifier == null) {
                return@combineTransform
            }
            emit(ScreenState.Loading)
            var hadErrors = false
            loadSchedule(
                identifier = params.identifier,
                loadOffline = params.cache,
                loadOnline = isOnline,
                startDate = startDate,
                endDate = endDate,
                onOfflineDaysSuccess = { days -> _days.emit(days) },
                onOfflineAttributeSuccess = { attribute -> _scheduleAttribute.emit(attribute) },
                onOnlineDaysSuccess = { days ->
                    println(this@ScheduleScreenViewModel.days.value)
                    _days.emit(days) },
                onOnlineAttributeSuccess = { attribute -> _scheduleAttribute.emit(attribute) },
                onOfflineDaysError = { hadErrors = true },
                onOfflineAttributeError = { hadErrors = true },
                onOnlineDaysError = { hadErrors = true },
                onOnlineAttributeError = { hadErrors = true }
            )
            emit(
                when {
                    hadErrors -> ScreenState.Error
                    !isOnline -> ScreenState.NoInternet
                    else -> ScreenState.Loaded
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ScreenState.Loading
        )

    val isMain: StateFlow<Boolean> = getScheduleIsMain(scheduleIdentifier).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )
    val isInFavorites: StateFlow<Boolean> = getScheduleIsFavorite(scheduleIdentifier).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    init {
        refresh()
    }

    fun getLectureState(scheduleDay: ScheduleDay, lecture: Lecture) =
        getLectureStateUseCase(scheduleDay, lecture).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LectureState.HasNotStarted
        )

    fun refresh() {
        viewModelScope.launch {
            refresh.emit(Unit)
        }
    }

    fun setDisplayMode(displayMode: ScheduleDisplayMode) {
        if (displayMode != this.displayMode.value &&
            displayMode == ScheduleDisplayMode.FREE_SCROLL
        ) {
            setFreeScrollDateRange(startDate.value, endDate.value)
        }
        savedStateHandle[DISPLAY_MODE_KEY] = displayMode
    }

    fun setFreeScrollDateRange(startDate: LocalDate, endDate: LocalDate) {
        savedStateHandle[START_DATE_KEY] = startDate
        savedStateHandle[END_DATE_KEY] = endDate
    }

    fun setAsMain() {
        viewModelScope.launch {
            val scheduleId = scheduleIdentifier.value
            if (scheduleId != null) {
                setMainSchedule(scheduleId)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val scheduleId = scheduleIdentifier.value
            if (scheduleId != null) {
                toggleScheduleFavorite(scheduleId)
            }
        }
    }

    private fun getStartDate(
        displayMode: ScheduleDisplayMode,
        currentDate: LocalDate,
        freeScrollStartDate: LocalDate
    ): LocalDate {
        return when (displayMode) {
            ScheduleDisplayMode.ONE_WEEK_FROM_TODAY -> currentDate
            ScheduleDisplayMode.CURRENT_WEEK -> currentDate.with(DayOfWeek.MONDAY)
            ScheduleDisplayMode.FREE_SCROLL -> freeScrollStartDate
        }
    }
    private fun getEndDate(
        displayMode: ScheduleDisplayMode,
        currentDate: LocalDate,
        freeScrollEndDate: LocalDate
    ): LocalDate {
        return when (displayMode) {
            ScheduleDisplayMode.ONE_WEEK_FROM_TODAY -> currentDate.plusDays(6)
            ScheduleDisplayMode.CURRENT_WEEK -> currentDate.with(DayOfWeek.MONDAY).plusDays(6)
            ScheduleDisplayMode.FREE_SCROLL -> freeScrollEndDate
        }
    }

    companion object {
        private const val DISPLAY_MODE_KEY = "displayMode"
        private const val START_DATE_KEY = "startDate"
        private const val END_DATE_KEY = "endDate"
    }
}

enum class ScheduleDisplayMode {
    FREE_SCROLL,
    CURRENT_WEEK,
    ONE_WEEK_FROM_TODAY
}
