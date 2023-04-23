package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.compose.material3.pullrefresh.rememberPullRefreshState
import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.schedule.LectureList
import aelsi2.natkschedule.ui.components.schedule.ScheduleScreenTopAppBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource

@Composable
fun RegularScheduleScreen(
    scheduleIdentifier: ScheduleIdentifier,
    onBackClick: () -> Unit,
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
        refreshThreshold = 64.dp,
        refreshingOffset = 64.dp
    )
    LaunchedEffect(true) {
        setUiState(topAppBar, topAppBarScrollBehavior.nestedScrollConnection, pullRefreshState, true)
    }

    Box(modifier = modifier.background(color = MaterialTheme.colorScheme.background)) {
        LectureList(
            days = days,
            viewModel::getLectureState,
            displayTeacher = identifier?.type != ScheduleType.Teacher,
            displayClassroom = identifier?.type != ScheduleType.Classroom,
            displayGroup = identifier?.type != ScheduleType.Group,
            displaySubgroup = identifier?.type == ScheduleType.Group,
            modifier = Modifier.fillMaxSize()
        )
    }
}