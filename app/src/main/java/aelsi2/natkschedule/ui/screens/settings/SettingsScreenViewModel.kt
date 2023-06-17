package aelsi2.natkschedule.ui.screens.settings

import aelsi2.natkschedule.data.preferences.FavoritesManager
import aelsi2.natkschedule.data.preferences.SettingsManager
import aelsi2.natkschedule.domain.use_cases.CleanCacheUseCase
import aelsi2.natkschedule.domain.use_cases.ClearSavedSchedulesUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val settingsManager: SettingsManager,
    private val favoritesManager: FavoritesManager,
    private val clearSavedSchedulesUseCase: ClearSavedSchedulesUseCase,
    private val cleanCache: CleanCacheUseCase
) : ViewModel() {
    val saveMainScheduleEnabled: StateFlow<Boolean> =
        settingsManager.saveMainScheduleEnabled.stateInViewModelScope(false)

    val saveFavoritesEnabled: StateFlow<Boolean> =
        settingsManager.saveFavoritesEnabled.stateInViewModelScope(false)

    val backgroundSyncEnabled: StateFlow<Boolean> =
        settingsManager.backgroundSyncEnabled.stateInViewModelScope(false)

    val backgroundSyncIntervalHours: StateFlow<Int> =
        settingsManager.backgroundSyncIntervalHours.stateInViewModelScope(0)

    val cleanOldSchedulesEnabled: StateFlow<Boolean> =
        settingsManager.cleanOldSchedulesEnabled.stateInViewModelScope(false)

    val savedScheduleMaxAgeDays: StateFlow<Int> =
        settingsManager.savedScheduleMaxAgeDays.stateInViewModelScope(0)

    val loaded: StateFlow<Boolean>
        get() = _loaded
    private val _loaded: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            settingsManager.waitForSettings()
            _loaded.emit(true)
        }
    }

    fun setSaveMainScheduleEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsManager.setSaveMainScheduleEnabled(value)
        }
    }

    fun setSaveFavoriteSchedulesEnabled(value: Boolean) {
        viewModelScope.launch {
            println(value)
            settingsManager.setSaveFavoritesEnabled(value)
        }
    }

    fun setBackgroundSyncEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsManager.setBackgroundSyncEnabled(value)
        }
    }

    fun setBackgroundSyncIntervalHours(value: Int) {
        viewModelScope.launch {
            settingsManager.setBackgroundSyncIntervalHours(value)
        }
    }

    fun setCleanOldSchedulesEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsManager.setCleanOldSchedulesEnabled(value)
        }
    }

    fun setSavedScheduleMaxAgeDays(value: Int) {
        viewModelScope.launch {
            settingsManager.setSavedScheduleMaxAgeDays(value)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            settingsManager.resetAll()
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            favoritesManager.clearFavorites()
        }
    }

    fun resetMainSchedule() {
        viewModelScope.launch {
            favoritesManager.resetMainScheduleId()
        }
    }

    fun clearSavedSchedules() {
        viewModelScope.launch {
            clearSavedSchedulesUseCase()
            cleanCache()
        }
    }

    private fun <T> Flow<T>.stateInViewModelScope(initialValue: T): StateFlow<T> = stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue
    )
}