package aelsi2.natkschedule.ui.components.schedule

import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.compose.material3.TopAppBarScrollBehavior
import aelsi2.compose.material3.TopAppBarWithBottomContent
import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.components.FilterChipRow
import aelsi2.natkschedule.ui.components.SelectableFilterChip
import aelsi2.natkschedule.ui.screens.schedule.ScheduleDisplayMode
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenTopAppBar(
    title: String,
    selectedDisplayMode: ScheduleDisplayMode,
    isInFavorites: Boolean,
    isMain: Boolean,
    modifier: Modifier = Modifier,
    titleIcon: ScheduleType? = null,
    backButtonVisible: Boolean = false,
    onBackClick: () -> Unit = {},
    onDetailsClick: () -> Unit = {},
    onDisplayModeSelected: (ScheduleDisplayMode) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onToggleFavoriteClick: () -> Unit = {},
    onSetMainClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onShowDateClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    TopAppBarWithBottomContent(
        title = {
            Crossfade(
                targetState = title
            ) { title ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title
                    )
                    if (titleIcon != null) {
                        val (iconDrawable, iconDescription) = when (titleIcon) {
                            ScheduleType.Classroom -> Pair(
                                R.drawable.door_outlined,
                                R.string.description_classroom
                            )

                            ScheduleType.Teacher -> Pair(
                                R.drawable.person_outlined,
                                R.string.description_teacher
                            )

                            ScheduleType.Group -> Pair(
                                R.drawable.people_outlined,
                                R.string.description_group
                            )
                        }
                        Icon(
                            painter = painterResource(iconDrawable),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = stringResource(iconDescription),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (backButtonVisible) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.back_arrow),
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        },
        actions = {
            var menuVisible: Boolean by remember { mutableStateOf(false) }
            IconButton(onClick = onDetailsClick) {
                Icon(
                    painter = painterResource(R.drawable.info_outlined),
                    contentDescription = stringResource(R.string.action_schedule_details)
                )
            }
            Box {
                IconButton(onClick = {
                    menuVisible = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.more_vertical),
                        contentDescription = stringResource(R.string.action_menu)
                    )
                }
                DropdownMenu(
                    expanded = menuVisible,
                    onDismissRequest = { menuVisible = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        clippingEnabled = false,
                    ),
                    modifier = Modifier.defaultMinSize(200.dp, 50.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.action_refresh))
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.refresh),
                                contentDescription = stringResource(R.string.action_refresh)
                            )
                        },
                        onClick = {
                            menuVisible = false
                            onRefreshClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.action_schedule_show_date))
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.event_outlined),
                                contentDescription = stringResource(R.string.action_schedule_show_date)
                            )
                        },
                        onClick = {
                            menuVisible = false
                            onShowDateClick()
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(
                                    when (isInFavorites) {
                                        false -> R.string.action_favorite_add
                                        true -> R.string.action_favorite_remove
                                    }
                                )
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    when (isInFavorites) {
                                        false -> R.drawable.star_outlined
                                        true -> R.drawable.star_filled
                                    }
                                ),
                                contentDescription = stringResource(
                                    when (isInFavorites) {
                                        false -> R.string.action_favorite_add
                                        true -> R.string.action_favorite_remove
                                    }
                                )
                            )
                        },
                        onClick = {
                            menuVisible = false
                            onToggleFavoriteClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.action_set_main_schedule))
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(
                                    when (isMain) {
                                        true -> R.drawable.home_filled
                                        false -> R.drawable.home_outlined
                                    }
                                ),
                                contentDescription = stringResource(R.string.action_set_main_schedule)
                            )
                        },
                        enabled = !isMain,
                        onClick = {
                            menuVisible = false
                            onSetMainClick()
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.action_settings))
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.settings_outlined),
                                contentDescription = stringResource(R.string.action_settings)
                            )
                        },
                        enabled = false,
                        onClick = {
                            menuVisible = false
                            onSettingsClick()
                        }
                    )
                }
            }
        },
        bottomContent = { windowInsets ->
            FilterChipRow(
                modifier = Modifier
                    .requiredHeight(52.dp)
                    .windowInsetsPadding(windowInsets)
            ) {
                SelectableFilterChip(
                    text = stringResource(id = R.string.filter_schedule_from_today),
                    selected = selectedDisplayMode == ScheduleDisplayMode.ONE_WEEK_FROM_TODAY,
                    onClick = {
                        onDisplayModeSelected(ScheduleDisplayMode.ONE_WEEK_FROM_TODAY)
                    }
                )
                SelectableFilterChip(
                    text = stringResource(id = R.string.filter_schedule_current_week),
                    selected = selectedDisplayMode == ScheduleDisplayMode.CURRENT_WEEK,
                    onClick = {
                        onDisplayModeSelected(ScheduleDisplayMode.CURRENT_WEEK)
                    }
                )
                SelectableFilterChip(
                    text = stringResource(id = R.string.filter_schedule_free_scroll),
                    selected = selectedDisplayMode == ScheduleDisplayMode.FREE_SCROLL,
                    onClick = {
                        onDisplayModeSelected(ScheduleDisplayMode.FREE_SCROLL)
                    }
                )
            }
        },
        maxBottomContentHeight = 52.dp,
        modifier = modifier,
        scrollBehavior = scrollBehavior
    )
}