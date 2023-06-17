package aelsi2.natkschedule.ui.screens.settings

import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.natkschedule.R
import aelsi2.natkschedule.ui.KEEP_SCHEDULE_FOR_OPTIONS_DAYS
import aelsi2.natkschedule.ui.SYNC_INTERVAL_OPTIONS_HOURS
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.ConfirmationDialog
import aelsi2.natkschedule.ui.components.InnerScaffold
import aelsi2.natkschedule.ui.components.ModalScreenTopAppBar
import aelsi2.natkschedule.ui.components.settings.ButtonSettingsItem
import aelsi2.natkschedule.ui.components.settings.DropdownSettingsItem
import aelsi2.natkschedule.ui.components.settings.SettingsCategoryHeader
import aelsi2.natkschedule.ui.components.settings.SettingsNote
import aelsi2.natkschedule.ui.components.settings.ToggleSettingsItem
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.pluralStringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: SettingsScreenViewModel = getViewModel()
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    InnerScaffold(
        topBar = {
            ModalScreenTopAppBar(
                title = stringResource(R.string.title_settings),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = onBackClick
            )
        },
        nestedScrollConnection = topAppBarScrollBehavior.nestedScrollConnection,
        modifier = modifier
    ) {
        LaunchedEffect(true) {
            setUiState({}, false)
        }
        val loaded by viewModel.loaded.collectAsState()
        AnimatedVisibility(visible = loaded) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                val saveMainScheduleEnabled by viewModel.saveMainScheduleEnabled.collectAsState()
                val saveFavoritesEnabled by viewModel.saveFavoritesEnabled.collectAsState()
                val backgroundSyncIntervalHours by viewModel.backgroundSyncIntervalHours.collectAsState()
                val backgroundSyncEnabled by viewModel.backgroundSyncEnabled.collectAsState()
                val cleanCacheAfter by viewModel.savedScheduleMaxAgeDays.collectAsState()
                val cleanCacheEnabled by viewModel.cleanOldSchedulesEnabled.collectAsState()

                OfflineSettings(
                    saveMainScheduleEnabled,
                    saveFavoritesEnabled,
                    cleanCacheEnabled,
                    cleanCacheAfter,
                    viewModel::setSaveMainScheduleEnabled,
                    viewModel::setSaveFavoriteSchedulesEnabled,
                    viewModel::setCleanOldSchedulesEnabled,
                    viewModel::setSavedScheduleMaxAgeDays,
                    viewModel::clearSavedSchedules
                )

                BackgroundSyncSettings(
                    saveMainScheduleEnabled,
                    saveFavoritesEnabled,
                    backgroundSyncEnabled,
                    backgroundSyncIntervalHours,
                    viewModel::setBackgroundSyncEnabled,
                    viewModel::setBackgroundSyncIntervalHours
                )

                ResetSettings(
                    viewModel::resetSettings,
                    viewModel::clearFavorites,
                    viewModel::resetMainSchedule
                )
            }
        }
    }
}

@Composable
private fun OfflineSettings(
    saveMainScheduleEnabled: Boolean,
    saveFavoritesEnabled: Boolean,
    cleanOldSchedulesEnabled: Boolean,
    scheduleHistoryLengthDays: Int,
    setSaveMainScheduleEnabled: (Boolean) -> Unit,
    setSaveFavoritesEnabled: (Boolean) -> Unit,
    setCleanOldSchedulesEnabled: (Boolean) -> Unit,
    setScheduleHistoryLengthDays: (Int) -> Unit,
    clearSavedSchedules: () -> Unit,
) {
    SettingsCategoryHeader(stringResource(R.string.settings_category_offline))

    ToggleSettingsItem(
        mainText = stringResource(R.string.setting_title_save_main_schedule),
        description = stringResource(R.string.setting_description_save_main_schedule),
        isChecked = saveMainScheduleEnabled,
        onCheckedChange = setSaveMainScheduleEnabled
    )

    ToggleSettingsItem(
        mainText = stringResource(R.string.setting_title_save_favorites),
        description = stringResource(R.string.setting_description_save_favorites),
        isChecked = saveFavoritesEnabled,
        onCheckedChange = setSaveFavoritesEnabled
    )

    var keepScheduleDropdownExpanded by remember { mutableStateOf(false) }
    DropdownSettingsItem(
        mainText = stringResource(R.string.setting_title_history_length_days),
        description = stringResource(R.string.setting_description_history_length_days),
        selectedItemText = if (cleanOldSchedulesEnabled) pluralStringResource(
            R.plurals.setting_format_history_length,
            count = scheduleHistoryLengthDays,
            scheduleHistoryLengthDays
        ) else stringResource(R.string.setting_format_history_length_unlimited),
        isExpanded = keepScheduleDropdownExpanded,
        isEnabled = saveMainScheduleEnabled || saveFavoritesEnabled,
        onExpandedChange = {
            keepScheduleDropdownExpanded = it
        }
    ) {
        for (option in KEEP_SCHEDULE_FOR_OPTIONS_DAYS) {
            DropdownMenuItem(
                text = {
                    Text(
                        pluralStringResource(
                            R.plurals.setting_format_history_length,
                            count = option,
                            option
                        )
                    )
                },
                onClick = {
                    keepScheduleDropdownExpanded = false
                    setCleanOldSchedulesEnabled(true)
                    setScheduleHistoryLengthDays(option)
                }
            )
        }
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.setting_format_history_length_unlimited))
            },
            onClick = {
                keepScheduleDropdownExpanded = false
                setCleanOldSchedulesEnabled(false)
            }
        )
    }
    var confirmClearDialogShown by remember { mutableStateOf(false) }
    ButtonSettingsItem(
        mainText = stringResource(R.string.setting_title_clear_saved_schedules),
        onClick = { confirmClearDialogShown = true }
    )
    if (confirmClearDialogShown) {
        ConfirmationDialog(
            onDismissRequest = {
                confirmClearDialogShown = false
            },
            titleText = stringResource(R.string.dialog_title_confirm_clear_saved_schedules),
            contentText = stringResource(R.string.dialog_content_confirm_clear_saved_schedules),
            iconResource = R.drawable.delete_outlined,
            onYesClick = clearSavedSchedules
        )
    }
}

@Composable
private fun BackgroundSyncSettings(
    saveMainScheduleEnabled: Boolean,
    saveFavoritesEnabled: Boolean,
    backgroundSyncEnabled: Boolean,
    backgroundSyncIntervalHours: Int,
    setBackgroundSyncEnabled: (Boolean) -> Unit,
    setBackgroundSyncIntervalHours: (Int) -> Unit
) {
    SettingsCategoryHeader(stringResource(R.string.settings_category_background_sync))

    ToggleSettingsItem(
        mainText = stringResource(R.string.setting_title_background_sync),
        description = stringResource(R.string.setting_description_background_sync),
        isChecked = backgroundSyncEnabled,
        isEnabled = saveMainScheduleEnabled || saveFavoritesEnabled,
        onCheckedChange = setBackgroundSyncEnabled
    )

    var syncIntervalDropdownExpanded by remember { mutableStateOf(false) }
    DropdownSettingsItem(
        mainText = stringResource(R.string.setting_title_background_sync_interval),
        description = stringResource(R.string.setting_description_background_sync_interval),
        selectedItemText = pluralStringResource(
            R.plurals.setting_format_background_sync_interval,
            count = backgroundSyncIntervalHours,
            backgroundSyncIntervalHours
        ),
        isExpanded = syncIntervalDropdownExpanded,
        isEnabled = backgroundSyncEnabled && (saveMainScheduleEnabled || saveFavoritesEnabled),
        onExpandedChange = {
            syncIntervalDropdownExpanded = it
        }
    ) {
        for (option in SYNC_INTERVAL_OPTIONS_HOURS) {
            DropdownMenuItem(
                text = {
                    Text(
                        pluralStringResource(
                            R.plurals.setting_format_background_sync_interval,
                            count = option,
                            option
                        )
                    )
                },
                onClick = {
                    syncIntervalDropdownExpanded = false
                    setBackgroundSyncIntervalHours(option)
                }
            )
        }
    }

    SettingsNote(stringResource(R.string.settings_note_background_sync))
}

@Composable
private fun ResetSettings(
    resetSettings: () -> Unit,
    clearFavorites: () -> Unit,
    resetMainSchedule: () -> Unit,
) {
    SettingsCategoryHeader(stringResource(R.string.settings_category_reset))

    var confirmResetSettingsDialogShown by remember { mutableStateOf(false) }
    ButtonSettingsItem(
        mainText = stringResource(R.string.setting_title_reset_settings),
        onClick = { confirmResetSettingsDialogShown = true }
    )
    if (confirmResetSettingsDialogShown) {
        ConfirmationDialog(
            onDismissRequest = {
                confirmResetSettingsDialogShown = false
            },
            titleText = stringResource(R.string.dialog_title_confirm_reset_settings),
            contentText = stringResource(R.string.dialog_content_confirm_reset_settings),
            iconResource = R.drawable.settings_outlined,
            onYesClick = resetSettings
        )
    }

    var confirmClearFavoritesDialogShown by remember { mutableStateOf(false) }
    ButtonSettingsItem(
        mainText = stringResource(R.string.setting_title_clear_favorites),
        onClick = { confirmClearFavoritesDialogShown = true }
    )
    if (confirmClearFavoritesDialogShown) {
        ConfirmationDialog(
            onDismissRequest = {
                confirmClearFavoritesDialogShown = false
            },
            titleText = stringResource(R.string.dialog_title_confirm_clear_favorites),
            contentText = stringResource(R.string.dialog_content_confirm_clear_favorites),
            iconResource = R.drawable.star_outlined,
            onYesClick = clearFavorites
        )
    }
}