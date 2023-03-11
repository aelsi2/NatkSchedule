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
    val displayMode =
        savedStateHandle.getStateFlow(DISPLAY_MODE_KEY, ScheduleDisplayMode.ONE_WEEK_FROM_TODAY)

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

    val lectures: StateFlow<List<GroupedLectureWithState>> =
        combineTransform<ScheduleParameters, Boolean, Unit, LocalDate, LocalDate, List<GroupedLectureWithState>>(
            getScheduleParameters(),
            networkMonitor.isOnline,
            update.conflate(),
            startDate,
            endDate
        ) { params, isOnline, _, startDate, endDate ->
            if (params.identifier == null) {
                return@combineTransform
            }
            if (isOnline && params.cache) {
                val localJob = viewModelScope.async {
                    localRepo.getLectures(startDate, endDate, params.identifier)
                }
                val networkJob = viewModelScope.async { networkRepo.getLectures(startDate, endDate, params.identifier) }
                val localResult = localJob.await()
                localResult.fold(
                    onSuccess = {
                        emit(groupLectures(it, viewModelScope))
                    },
                    onFailure = {
                        // TODO
                    }
                )
                val networkResult = networkJob.await()
                localJob.cancel()
                networkResult.fold(
                    onSuccess = {
                        localRepo.putLectures(startDate, endDate, params.identifier, it)
                        emit(groupLectures(it, viewModelScope))
                    },
                    onFailure = {
                        // TODO
                    }
                )
            }
            else if (params.cache) {
                val localResult = localRepo.getLectures(startDate, endDate, params.identifier)
                localResult.fold(
                    onSuccess = {
                        emit(groupLectures(it, viewModelScope))
                    },
                    onFailure = {
                        // TODO
                    }
                )
            }
            else if (isOnline) {
                val result = networkRepo.getLectures(startDate, endDate, params.identifier)
                result.fold(
                    onSuccess = {
                        emit(groupLectures(it, viewModelScope))
                    },
                    onFailure = {
                        // TODO
                    }
                )
            }
            else {
                // TODO
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

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
}

