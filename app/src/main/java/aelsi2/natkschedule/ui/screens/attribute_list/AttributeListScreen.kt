package aelsi2.natkschedule.ui.screens.attribute_list

import aelsi2.compose.material3.appbar.TopAppBarDefaults
import aelsi2.compose.material3.pullrefresh.rememberPullRefreshState
import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.AttributeList
import aelsi2.natkschedule.ui.components.AttributeListTopAppBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeacherListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: TeacherListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_teacher_list),
        filters = {},
        onAttributeClick = onAttributeClick,
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
fun ClassroomListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: ClassroomListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_classroom_list),
        filters = {},
        onAttributeClick = onAttributeClick,
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
fun GroupListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: GroupListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_group_list),
        filters = {},
        onAttributeClick = onAttributeClick,
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributeListScreen(
    title: String,
    filters: @Composable () -> Unit,
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier,
    viewModel: AttributeListScreenViewModel
) {
    val attributes by viewModel.attributes.collectAsState()
    val state by viewModel.state.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state == ScreenState.Loading,
        onRefresh = viewModel::refresh,
        refreshThreshold = 48.dp,
        refreshingOffset = 48.dp
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topAppBar: @Composable () -> Unit = remember {
        {
            AttributeListTopAppBar(
                title = title,
                onRefreshClick = viewModel::refresh,
                scrollBehavior = scrollBehavior
            )
        }
    }
    LaunchedEffect(key1 = true){
        setUiState(topAppBar, scrollBehavior.nestedScrollConnection, pullRefreshState, true)
    }
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AttributeList(
            attributes = attributes,
            onAttributeClick = onAttributeClick,
            modifier = modifier.fillMaxSize(),
            filters = filters
        )
    }
}