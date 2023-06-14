package aelsi2.natkschedule.ui.screens.schedule.main

import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.data.time.TimeManager
import aelsi2.natkschedule.domain.use_cases.GetLectureStateUseCase
import aelsi2.natkschedule.domain.use_cases.GetMainScheduleIsSetUseCase
import aelsi2.natkschedule.domain.use_cases.GetScheduleIsFavoriteUseCase
import aelsi2.natkschedule.domain.use_cases.GetScheduleIsMainUseCase
import aelsi2.natkschedule.domain.use_cases.GetScheduleParametersUseCase
import aelsi2.natkschedule.domain.use_cases.LoadScheduleUseCase
import aelsi2.natkschedule.domain.use_cases.SetMainScheduleUseCase
import aelsi2.natkschedule.domain.use_cases.ToggleScheduleFavoriteUseCase
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreenViewModel
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@Stable
class MainScheduleScreenViewModel(
    getScheduleParameters: GetScheduleParametersUseCase,
    getScheduleIsMain: GetScheduleIsMainUseCase,
    getScheduleIsFavorite: GetScheduleIsFavoriteUseCase,
    networkMonitor: NetworkMonitor,
    timeManager: TimeManager,
    savedStateHandle: SavedStateHandle,
    loadSchedule: LoadScheduleUseCase,
    getLectureStateUseCase: GetLectureStateUseCase,
    setMainSchedule: SetMainScheduleUseCase,
    toggleScheduleFavorite: ToggleScheduleFavoriteUseCase,
    getMainScheduleSet: GetMainScheduleIsSetUseCase,
) : ScheduleScreenViewModel(
    getScheduleParameters,
    getScheduleIsMain,
    getScheduleIsFavorite,
    networkMonitor,
    timeManager,
    savedStateHandle,
    loadSchedule,
    getLectureStateUseCase,
    setMainSchedule,
    toggleScheduleFavorite
) {
    val mainScheduleSet: StateFlow<Boolean> =
        getMainScheduleSet().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )
}