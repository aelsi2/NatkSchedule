package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.domain.ScreenState
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
import java.time.temporal.TemporalAdjusters

class ScheduleScreenViewModel(
    getScheduleParameters: GetScheduleParametersUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val loadSchedule: LoadScheduleUseCase,
    private val getLectureStateUseCase: GetLectureStateUseCase,
    networkMonitor: NetworkMonitor,
    private val timeManager: TimeManager
) : ViewModel() {
    private val update = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val displayMode =
        savedStateHandle.getStateFlow(DISPLAY_MODE_KEY, ScheduleDisplayMode.ONE_WEEK_FROM_TODAY)

    val scheduleIdentifier: StateFlow<ScheduleIdentifier?> = getScheduleParameters().map {
        it.identifier
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val scheduleAttribute: StateFlow<ScheduleAttribute?>
        get() = _scheduleAttribute
    private val _scheduleAttribute = MutableStateFlow<ScheduleAttribute?>(null)

    private val startDate = savedStateHandle.getStateFlow(
        START_DATE_KEY, timeManager.currentCollegeLocalDate
    )
    private val endDate = savedStateHandle.getStateFlow(
        END_DATE_KEY,
        timeManager.currentCollegeLocalDate.plusDays(7)
    )

    val days: StateFlow<List<ScheduleDay>>
        get() = _days
    private val _days = MutableStateFlow<List<ScheduleDay>>(listOf())

    val state: StateFlow<ScreenState> =
        combineTransform<ScheduleParameters, Boolean, Unit, LocalDate, LocalDate, ScreenState>(
            getScheduleParameters(),
            networkMonitor.isOnline,
            update.conflate(),
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
                onOfflineDaysSuccess = {days -> _days.emit(days) },
                onOfflineAttributeSuccess = {attribute -> _scheduleAttribute.emit(attribute) },
                onOnlineDaysSuccess = {days -> _days.emit(days) },
                onOnlineAttributeSuccess = {attribute -> _scheduleAttribute.emit(attribute) },
                onOfflineDaysError = { hadErrors = true },
                onOfflineAttributeError = { hadErrors = true },
                onOnlineDaysError = { hadErrors = true },
                onOnlineAttributeError = { hadErrors = true }
            )
            emit(when {
                hadErrors -> ScreenState.Error
                !isOnline -> ScreenState.NoInternet
                else -> ScreenState.Loaded
            })
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ScreenState.Loading
        )

    @Stable
    fun getLectureState(scheduleDay: ScheduleDay, lecture: Lecture) =
        getLectureStateUseCase(scheduleDay, lecture).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            LectureState.HasNotStarted
        )

    init {
        viewModelScope.launch {
            displayMode.collect { displayMode ->
                when (displayMode) {
                    ScheduleDisplayMode.ONE_WEEK_FROM_TODAY -> {
                        setDateRange(
                            timeManager.currentCollegeLocalDate,
                            timeManager.currentCollegeLocalDate.plusDays(6)
                        )
                    }
                    ScheduleDisplayMode.CURRENT_WEEK -> {
                        setDateRange(
                            timeManager.currentCollegeLocalDate.with(
                                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                            ),
                            startDate.value.plusDays(6)
                        )
                    }
                    else -> Unit
                }
            }
        }
        update()
    }

    fun loadPreviousWeek() {
        if (displayMode.value != ScheduleDisplayMode.FREE_SCROLLING) {
            return
        }
        val newStartDate = startDate.value.minusDays(7)
        setDateRange(
            newStartDate,
            newStartDate.plusDays(14)
        )
    }
    fun loadNextWeek() {
        if (displayMode.value != ScheduleDisplayMode.FREE_SCROLLING) {
            return
        }
        val newEndDate = endDate.value.plusDays(7)
        setDateRange(
            newEndDate.minusDays(14),
            newEndDate
        )
    }

    fun update() {
        viewModelScope.launch {
            update.emit(Unit)
        }
    }

    private fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        savedStateHandle[START_DATE_KEY] = startDate
        savedStateHandle[END_DATE_KEY] = endDate
    }

    companion object {
        private const val DISPLAY_MODE_KEY = "displayMode"
        private const val START_DATE_KEY = "startDate"
        private const val END_DATE_KEY = "endDate"
    }
}

enum class ScheduleDisplayMode {
    FREE_SCROLLING,
    CURRENT_WEEK,
    ONE_WEEK_FROM_TODAY
}
