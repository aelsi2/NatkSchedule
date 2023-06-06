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
import android.os.Parcel
import android.os.Parcelable
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
open class ScheduleScreenViewModel(
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

    val displayMode =
        savedStateHandle.getStateFlow(DISPLAY_MODE_KEY, ScheduleDisplayMode.WeekFromToday)

    private val freeScrollDateRange: StateFlow<DateRange> = savedStateHandle.getStateFlow(
        FREE_SCROLL_DATE_RANGE_KEY,
        DateRange(
            timeManager.currentCollegeLocalDate,
            timeManager.currentCollegeLocalDate.plusDays(6)
        )
    )
    val loadedDateRange: StateFlow<DateRange> = combine(
        displayMode,
        timeManager.collegeLocalDate.conflate(),
        freeScrollDateRange
    ) { displayMode, currentDate, freeScrollRange ->
        when (displayMode) {
            ScheduleDisplayMode.WeekFromToday -> DateRange(
                currentDate,
                currentDate.plusDays(6)
            )
            ScheduleDisplayMode.WeekCurrent -> DateRange(
                currentDate.with(DayOfWeek.MONDAY),
                currentDate.with(DayOfWeek.MONDAY).plusDays(6)
            )
            ScheduleDisplayMode.FreeScroll -> freeScrollRange
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DateRange(
            timeManager.currentCollegeLocalDate,
            timeManager.currentCollegeLocalDate.plusDays(6)
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
            loadedDateRange,
            refresh.conflate()
        ) { params, isOnline, dateRange, _ ->
            if (params.identifier == null) {
                emit(ScreenState.Loaded)
                return@combineTransform
            }
            emit(ScreenState.Loading)
            var hadErrors = false
            loadSchedule(
                identifier = params.identifier,
                useLocalRepo = params.cache,
                useNetworkRepo = isOnline,
                startDate = dateRange.startDate,
                endDate = dateRange.endDate,
                onSuccess = { attribute, days ->
                    if (attribute != null) {
                        _scheduleAttribute.emit(attribute)
                    }
                    if (days != null) {
                        _days.emit(days)
                    }
                },
                onFailure = {
                    hadErrors = true
                }
            )
            emit(
                when {
                    hadErrors -> ScreenState.Error
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
            LectureState.NotStarted
        )

    fun refresh() {
        viewModelScope.launch {
            refresh.emit(Unit)
        }
    }

    fun setDisplayMode(displayMode: ScheduleDisplayMode) {
        if (displayMode != this.displayMode.value &&
            displayMode == ScheduleDisplayMode.FreeScroll
        ) {
            setFreeScrollDateRange(loadedDateRange.value)
        }
        savedStateHandle[DISPLAY_MODE_KEY] = displayMode
    }

    fun setFreeScrollDateRange(range: DateRange) {
        savedStateHandle[FREE_SCROLL_DATE_RANGE_KEY] = range
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

    companion object {
        private const val DISPLAY_MODE_KEY = "displayMode"
        private const val FREE_SCROLL_DATE_RANGE_KEY = "freeScrollDateRange"
    }
}

enum class ScheduleDisplayMode {
    FreeScroll,
    WeekCurrent,
    WeekFromToday
}

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
) : Parcelable {
    private constructor(parcel: Parcel) : this(
        LocalDate.ofEpochDay(parcel.readLong()),
        LocalDate.ofEpochDay(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(startDate.toEpochDay())
        parcel.writeLong(endDate.toEpochDay())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DateRange> {
        override fun createFromParcel(parcel: Parcel): DateRange {
            return DateRange(parcel)
        }

        override fun newArray(size: Int): Array<DateRange?> {
            return arrayOfNulls(size)
        }
    }

}