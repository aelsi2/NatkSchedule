package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.data.repositories.LectureRepository
import aelsi2.natkschedule.data.repositories.WritableLectureRepository
import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.domain.GetScheduleParametersUseCase
import aelsi2.natkschedule.domain.GroupLecturesUseCase
import aelsi2.natkschedule.domain.ScheduleParameters
import aelsi2.natkschedule.domain.model.GroupedLectureWithState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class ScheduleScreenViewModel(
    private val networkRepo: LectureRepository,
    private val localRepo: WritableLectureRepository,
    networkMonitor: NetworkMonitor,
    getScheduleParameters: GetScheduleParametersUseCase,
    private val groupLectures: GroupLecturesUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val timeManager: TimeManager
) : ViewModel() {
    val displayMode =
        savedStateHandle.getStateFlow(DISPLAY_MODE_KEY, ScheduleDisplayMode.ONE_WEEK_FROM_TODAY)
    private val startDate = savedStateHandle.getStateFlow(
        START_DATE_KEY, timeManager.currentCollegeLocalDate
    )
    private val endDate = savedStateHandle.getStateFlow(
        END_DATE_KEY,
        timeManager.currentCollegeLocalDate.plusDays(7)
    )

    private val update = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val lectures: StateFlow<List<GroupedLectureWithState>>
        get() = _lectures
    private val _lectures = MutableStateFlow<List<GroupedLectureWithState>>(listOf())

    val state: StateFlow<ScheduleState> =
        combineTransform<ScheduleParameters, Boolean, Unit, LocalDate, LocalDate, ScheduleState>(
            getScheduleParameters(),
            networkMonitor.isOnline,
            update.conflate(),
            startDate,
            endDate
        ) { params, isOnline, _, startDate, endDate ->
            if (params.identifier == null) {
                return@combineTransform
            }
            val localJob = if (params.cache) viewModelScope.async {
                localRepo.getLectures(startDate, endDate, params.identifier)
            } else null
            val networkJob = if (isOnline) viewModelScope.async {
                networkRepo.getLectures(startDate, endDate, params.identifier)
            } else null
            if (params.cache) {
                emit(ScheduleState.Loading)
                val localResult = localJob!!.await()
                localResult.fold(
                    onSuccess = {
                        _lectures.emit(groupLectures(it, viewModelScope))
                        emit(ScheduleState.Loaded)
                    },
                    onFailure = { }
                )
            }
            if (isOnline) {
                emit(ScheduleState.Loading)
                val networkResult = networkJob!!.await()
                if (params.cache) {
                    localJob!!.cancel()
                }
                networkResult.fold(
                    onSuccess = {
                        localRepo.putLectures(startDate, endDate, params.identifier, it)
                        _lectures.emit(groupLectures(it, viewModelScope))
                        emit(ScheduleState.Loaded)
                    },
                    onFailure = {
                        emit(ScheduleState.Error)
                    }
                )
            }
            if (!isOnline && !params.cache) {
                emit(ScheduleState.NoInternet)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScheduleState.Loading)

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

sealed interface ScheduleState {
    object Loading : ScheduleState
    object Error : ScheduleState
    object Loaded : ScheduleState
    object NoInternet : ScheduleState
}

