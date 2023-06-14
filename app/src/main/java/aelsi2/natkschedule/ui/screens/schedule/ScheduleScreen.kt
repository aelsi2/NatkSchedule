package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.compose.material3.pullrefresh.rememberPullRefreshState
import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.PULL_REFRESH_OFFSET
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.InnerScaffold
import aelsi2.natkschedule.ui.components.schedule.LectureInfoDialog
import aelsi2.natkschedule.ui.components.schedule.LectureList
import aelsi2.natkschedule.ui.components.schedule.ScheduleInfoDialog
import aelsi2.natkschedule.ui.components.schedule.ScheduleScreenTopAppBar
import aelsi2.natkschedule.ui.components.schedule.rememberLectureInfoDialogState
import aelsi2.natkschedule.ui.components.schedule.rememberScheduleInfoDialogState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource

@Composable
fun RegularScheduleScreen(
    scheduleIdentifier: ScheduleIdentifier,
    onBackClick: () -> Unit,
    onScheduleClick: (ScheduleIdentifier) -> Unit,
    onSettingsClick: () -> Unit,
    onError: suspend () -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: ScheduleScreenViewModel = koinViewModel(
        parameters = { parametersOf(scheduleIdentifier) }
    )
) {
    ScheduleScreen(
        backButtonVisible = true,
        onBackClick = onBackClick,
        onScheduleClick = onScheduleClick,
        onSettingsClick = onSettingsClick,
        onError = onError,
        viewModel = viewModel,
        setUiState = setUiState,
        modifier = modifier
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    backButtonVisible: Boolean,
    onBackClick: () -> Unit,
    onScheduleClick: (ScheduleIdentifier) -> Unit,
    onSettingsClick: () -> Unit,
    onError: suspend () -> Unit,
    viewModel: ScheduleScreenViewModel,
    setUiState: SetUiStateLambda,
    modifier: Modifier,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state) {
        when (state) {
            ScreenState.Error -> onError()
            else -> Unit
        }
    }

    val days by viewModel.days.collectAsState()
    val identifier by viewModel.scheduleIdentifier.collectAsState()
    val isMain by viewModel.isMain.collectAsState()
    val isInFavorites by viewModel.isInFavorites.collectAsState()
    val attribute by viewModel.scheduleAttribute.collectAsState()
    val displayMode by viewModel.displayMode.collectAsState()

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pullRefreshState = rememberPullRefreshState(
        state == ScreenState.Loading,
        viewModel::refresh,
        refreshThreshold = PULL_REFRESH_OFFSET,
        refreshingOffset = PULL_REFRESH_OFFSET
    )
    val lazyListState = rememberLazyListState()

    LaunchedEffect(true) {
        setUiState({}, true)
    }

    val lectureInfoDialogState = rememberLectureInfoDialogState(
        getLectureState = viewModel::getLectureState
    )
    LectureInfoDialog(
        state = lectureInfoDialogState,
        onDismissRequest = lectureInfoDialogState::hide,
        onScheduleClick = {schedule ->
            lectureInfoDialogState.hide()
            onScheduleClick(schedule)
        }
    )

    val scheduleInfoDialogState = rememberScheduleInfoDialogState()

    ScheduleInfoDialog(
        state = scheduleInfoDialogState,
        onDismissRequest = scheduleInfoDialogState::hide
    )

    InnerScaffold(
        modifier = modifier,
        nestedScrollConnection = topAppBarScrollBehavior.nestedScrollConnection,
        pullRefreshState = pullRefreshState,
        topBar = {
            val attributeName = attribute?.displayName ?: stringResource(R.string.title_loading)
            ScheduleScreenTopAppBar(
                title = attributeName,
                titleIcon = identifier?.type,
                backButtonVisible = backButtonVisible,
                onBackClick = onBackClick,
                onDetailsClick = {
                    val currentAttribute = attribute
                    if (currentAttribute != null) {
                        scheduleInfoDialogState.show(currentAttribute)
                    }
                },
                selectedDisplayMode = displayMode,
                isInFavorites = isInFavorites,
                isMain = isMain,
                onSettingsClick = onSettingsClick,
                onRefreshClick = viewModel::refresh,
                onDisplayModeSelected = viewModel::setDisplayMode,
                onToggleFavoriteClick = viewModel::toggleFavorite,
                onSetMainClick = viewModel::setAsMain,
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) {
        LectureList(
            scheduleDays = days,
            viewModel::getLectureState,
            displayTeacher = identifier?.type != ScheduleType.Teacher,
            displayClassroom = identifier?.type != ScheduleType.Classroom,
            displayGroup = identifier?.type != ScheduleType.Group,
            displaySubgroup = identifier?.type == ScheduleType.Group,
            modifier = Modifier.fillMaxSize(),
            lazyListState = lazyListState,
            onLectureClick = { lecture, day ->
                lectureInfoDialogState.show(lecture, day)
            }
        )
    }
}

