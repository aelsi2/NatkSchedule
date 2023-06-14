package aelsi2.natkschedule.ui.screens.settings

import aelsi2.natkschedule.data.preferences.SettingsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {
    val saveMainScheduleEnabled: StateFlow<Boolean> =
        settingsManager.saveMainScheduleEnabled.stateInViewModelScope(false)

    val saveFavoritesEnabled: StateFlow<Boolean> =
        settingsManager.saveFavoritesEnabled.stateInViewModelScope(false)

    val backgroundSyncEnabled: StateFlow<Boolean> =
        settingsManager.backgroundSyncEnabled.stateInViewModelScope(false)

    val backgroundSyncIntervalHours: StateFlow<Int> =
        settingsManager.backgroundSyncIntervalHours.stateInViewModelScope(0)

    val cleanCacheOnSyncEnabled: StateFlow<Boolean> =
        settingsManager.cleanCacheOnSyncEnabled.stateInViewModelScope(false)

    val cleanCacheOnStartupEnabled: StateFlow<Boolean> =
        settingsManager.cleanCacheOnStartupEnabled.stateInViewModelScope(false)

    val keepScheduleForDays: StateFlow<Int> =
        settingsManager.keepScheduleForDays.stateInViewModelScope(0)

    fun setCacheMainScheduleEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsManager.setSaveMainScheduleEnabled(value)
        }
    }

    fun setCacheFavoriteSchedulesEnabled(value: Boolean) {
        viewModelScope.launch {
            println(value)
            settingsManager.setSaveFavoritesEnabled(value)
        }
    }

    fun setBackgroundSyncEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsManager.setCacheBackgroundSyncEnabled(value)
        }
    }

    fun setBackgroundSyncIntervalHours(value: Int) {
        viewModelScope.launch {
            settingsManager.setBackgroundSyncIntervalHours(value)
        }
    }

    fun setCleanCacheOnStartupEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsManager.setCleanCacheOnStartupEnabled(value)
        }
    }

    fun setCleanCacheOnSyncEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsManager.setCleanCacheOnSyncEnabled(value)
        }
    }

    fun setKeepScheduleForDays(value: Int) {
        viewModelScope.launch {
            settingsManager.setKeepScheduleForDays(value)
        }
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            settingsManager.resetAll()
        }
    }

    private fun <T> Flow<T>.stateInViewModelScope(initialValue: T): StateFlow<T> = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue
    )
}