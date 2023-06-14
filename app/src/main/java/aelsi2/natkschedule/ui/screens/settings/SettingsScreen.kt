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
import aelsi2.natkschedule.ui.components.settings.ToggleSettingsItem
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            val saveMainScheduleEnabled by viewModel.saveMainScheduleEnabled.collectAsState()
            val saveFavoritesEnabled by viewModel.saveFavoritesEnabled.collectAsState()
            val cleanCacheOnStartupEnabled by viewModel.cleanCacheOnStartupEnabled.collectAsState()
            val backgroundSyncIntervalHours by viewModel.backgroundSyncIntervalHours.collectAsState()
            val backgroundSyncEnabled by viewModel.backgroundSyncEnabled.collectAsState()
            val keepScheduleForDays by viewModel.keepScheduleForDays.collectAsState()
            val cleanCacheOnSyncEnabled by viewModel.cleanCacheOnSyncEnabled.collectAsState()
            SettingsCategoryHeader(stringResource(R.string.settings_category_caching))

            ToggleSettingsItem(
                mainText = stringResource(R.string.setting_title_save_main_schedule),
                description = stringResource(R.string.setting_description_save_main_schedule),
                isChecked = saveMainScheduleEnabled,
                onCheckedChange = viewModel::setCacheMainScheduleEnabled
            )

            ToggleSettingsItem(
                mainText = stringResource(R.string.setting_title_save_favorites),
                description = stringResource(R.string.setting_description_save_favorites),
                isChecked = saveFavoritesEnabled,
                onCheckedChange = viewModel::setCacheFavoriteSchedulesEnabled
            )

            ToggleSettingsItem(
                mainText = stringResource(R.string.setting_title_clean_cache_on_startup),
                description = stringResource(R.string.setting_description_clean_cache_on_startup),
                isChecked = cleanCacheOnStartupEnabled,
                onCheckedChange = viewModel::setCleanCacheOnStartupEnabled
            )

            var keepScheduleDropdownExpanded by remember { mutableStateOf(false) }
            DropdownSettingsItem(
                mainText = stringResource(R.string.setting_title_keep_schedule_days),
                description = stringResource(R.string.setting_description_keep_schedule_days),
                selectedItemText = pluralStringResource(
                    R.plurals.setting_format_keep_schedule_days,
                    count = keepScheduleForDays,
                    keepScheduleForDays
                ),
                isExpanded = keepScheduleDropdownExpanded,
                isEnabled = cleanCacheOnStartupEnabled
                        || backgroundSyncEnabled && cleanCacheOnSyncEnabled,
                onExpandedChange = {
                    keepScheduleDropdownExpanded = it
                }
            ) {
                for (option in KEEP_SCHEDULE_FOR_OPTIONS_DAYS) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                pluralStringResource(
                                    R.plurals.setting_format_keep_schedule_days,
                                    count = option,
                                    option
                                )
                            )
                        },
                        onClick = {
                            keepScheduleDropdownExpanded = false
                            viewModel.setKeepScheduleForDays(option)
                        }
                    )
                }
            }

            SettingsCategoryHeader(stringResource(R.string.settings_category_background_sync))

            ToggleSettingsItem(
                mainText = stringResource(R.string.setting_title_background_sync),
                description = stringResource(R.string.setting_description_background_sync),
                isChecked = backgroundSyncEnabled,
                onCheckedChange = viewModel::setBackgroundSyncEnabled
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
                isEnabled = backgroundSyncEnabled,
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
                            viewModel.setBackgroundSyncIntervalHours(option)
                        }
                    )
                }
            }

            ToggleSettingsItem(
                mainText = stringResource(R.string.setting_title_clean_cache_on_sync),
                description = stringResource(R.string.setting_description_clean_cache_on_sync),
                isChecked = cleanCacheOnSyncEnabled,
                isEnabled = backgroundSyncEnabled,
                onCheckedChange = viewModel::setCleanCacheOnSyncEnabled
            )

            SettingsCategoryHeader(stringResource(R.string.settings_category_reset))

            var confirmResetDialogShown by remember { mutableStateOf(false) }
            ButtonSettingsItem(
                mainText = stringResource(R.string.setting_title_reset_settings),
                onClick = { confirmResetDialogShown = true}
            )
            if (confirmResetDialogShown) {
                ConfirmationDialog(
                    onDismissRequest = {
                        confirmResetDialogShown = false
                    },
                    titleText = stringResource(R.string.dialog_title_confirm_reset_settings),
                    contentText = stringResource(R.string.dialog_content_confirm_reset_settings),
                    iconResource = R.drawable.settings_outlined,
                    onYesClick = viewModel::resetAllSettings
                )
            }
        }
    }
}