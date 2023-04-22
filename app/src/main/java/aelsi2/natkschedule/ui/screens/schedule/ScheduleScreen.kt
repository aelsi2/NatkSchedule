package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.compose.material3.pullrefresh.rememberPullRefreshState
import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.BasicTopAppBar
import aelsi2.natkschedule.ui.components.LectureList
import aelsi2.natkschedule.ui.components.ScheduleScreenTopAppBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun MainScheduleScreen(
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: ScheduleScreenViewModel = koinViewModel(qualifier = named("main"))
) {
    val scheduleIdentifier by viewModel.scheduleIdentifier.collectAsState()
    if (scheduleIdentifier == null) {
        MainScheduleNotSetScreen(setUiState = setUiState, modifier = modifier)
    }
    else {
        ScheduleScreen(
            backButtonVisible = false,
            onBackClick = { },
            viewModel = viewModel,
            setUiState = setUiState,
            modifier = modifier
        )
    }
}
@Composable
fun RegularScheduleScreen(
    scheduleIdentifier: ScheduleIdentifier,
    onBackClick: () -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: ScheduleScreenViewModel = koinViewModel(
        qualifier = named("regular"),
        parameters = { parametersOf(scheduleIdentifier) }
    )
) {
    ScheduleScreen(
        backButtonVisible = true,
        onBackClick = onBackClick,
        viewModel = viewModel,
        setUiState = setUiState,
        modifier = modifier
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleScreen(
    backButtonVisible: Boolean,
    onBackClick: () -> Unit,
    viewModel: ScheduleScreenViewModel,
    setUiState: SetUiStateLambda,
    modifier: Modifier,
) {
    val days by viewModel.days.collectAsState()
    val state by viewModel.state.collectAsState()
    val identifier by viewModel.scheduleIdentifier.collectAsState()
    val isMain by viewModel.isMain.collectAsState()
    val isInFavorites by viewModel.isInFavorites.collectAsState()
    val attribute by viewModel.scheduleAttribute.collectAsState()
    val displayMode by viewModel.displayMode.collectAsState()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topAppBar: @Composable () -> Unit = remember {
        {
            val attributeName = attribute?.displayName ?: stringResource(R.string.title_loading)
            ScheduleScreenTopAppBar(
                title = attributeName,
                titleIcon = identifier?.type,
                backButtonVisible = backButtonVisible,
                onBackClick = onBackClick,
                selectedDisplayMode = displayMode,
                isInFavorites = isInFavorites,
                isMain = isMain,
                onRefreshClick = viewModel::refresh,
                onDisplayModeSelected = viewModel::setDisplayMode,
                onToggleFavoriteClick = viewModel::toggleFavorite,
                onSetMainClick = viewModel::setAsMain,
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    }
    val pullRefreshState = rememberPullRefreshState(
        state == ScreenState.Loading,
        viewModel::refresh,
        refreshThreshold = 48.dp,
        refreshingOffset = 48.dp
    )
    LaunchedEffect(true) {
        setUiState(topAppBar, topAppBarScrollBehavior.nestedScrollConnection, pullRefreshState, true)
    }

    Box(modifier = modifier.background(color = MaterialTheme.colorScheme.background)) {
        LectureList(
            days = days,
            viewModel::getLectureState,
            displayTeacher = identifier?.type != ScheduleType.TEACHER,
            displayClassroom = identifier?.type != ScheduleType.CLASSROOM,
            displayGroup = identifier?.type != ScheduleType.GROUP,
            displaySubgroup = identifier?.type == ScheduleType.GROUP,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScheduleNotSetScreen(
    setUiState: SetUiStateLambda,
    modifier: Modifier
) {
    val topAppBar: @Composable () -> Unit = remember {
        {
            BasicTopAppBar(
                title = stringResource(R.string.title_home),
                onSettingsClick = {

                }
            )
        }
    }
    LaunchedEffect(true) {
        setUiState(topAppBar, null, null, true)
    }
    Box(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surface)) {
        Text(
            text = rememberStringWithInlineContent(R.string.message_main_schedule_not_set),
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.Center),
            inlineContent = rememberInlineIcons(remember { mapOf(
                Pair(0, Pair(R.drawable.people_outlined, R.string.groups_tab_name)),
                Pair(1, Pair(R.drawable.person_outlined, R.string.teachers_tab_name)),
                Pair(2, Pair(R.drawable.door_outlined, R.string.classrooms_tab_name)),
                Pair(3, Pair(R.drawable.more_vertical, R.string.action_menu))
            )}, iconSize = 16.sp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun rememberInlineIcons(
    icons: Map<Int, Pair<Int, Int>>,
    iconSize: TextUnit = 14.sp,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant
): Map<String, InlineTextContent> {
    return remember(icons, iconSize, iconTint) {
        val map: MutableMap<String, InlineTextContent> = mutableMapOf()
        for (entry in icons) {
            map[entry.key.toString()] = InlineTextContent(
                Placeholder(
                    width = iconSize,
                    height = iconSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    painter = painterResource(entry.value.first),
                    contentDescription = stringResource(entry.value.second),
                    tint = iconTint
                )
            }
        }
        map
    }
}

@Composable
fun rememberStringWithInlineContent(id: Int): AnnotatedString {
    val rawText = stringResource(id)
    return remember(key1 = rawText) {
        getStringWithInlineContent(rawText)
    }
}

private val iconPlaceholderNumberRegex = Regex("(?<!\\\\)(?<=\\\$inline\\[)[0-9]{1,5}(?=])")
private val iconPlaceholderSplitRegex = Regex("((?<!\\\\)(?<=\\\$inline\\[[0-9]{1,5}]))|((?!\\\\)(?=\\\$inline\\[[0-9]{1,5}]))")
private val escapeRegex = Regex("\\\\(?=\\\$inline)")

fun getStringWithInlineContent(rawString: String): AnnotatedString = buildAnnotatedString {
    val strings = iconPlaceholderSplitRegex.split(rawString)
    for (string in strings) {
        val match = iconPlaceholderNumberRegex.find(string)
        if (match != null) {
            appendInlineContent(match.value)
        }
        else {
            append(escapeRegex.replace(string, ""))
        }
    }
}