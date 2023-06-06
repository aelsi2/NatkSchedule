package aelsi2.natkschedule.ui.screens.schedule.main

import aelsi2.compose.material3.rememberInlineIcons
import aelsi2.compose.material3.stringResourceWithInlineContent
import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.BasicTopAppBar
import aelsi2.natkschedule.ui.components.InnerScaffold
import aelsi2.natkschedule.ui.screens.schedule.ScheduleScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScheduleScreen(
    setUiState: SetUiStateLambda,
    onScheduleClick: (ScheduleIdentifier) -> Unit,
    modifier: Modifier = Modifier,
    onError: suspend () -> Unit = {},
    viewModel: MainScheduleScreenViewModel = koinViewModel()
) {
    val mainScheduleSet by viewModel.mainScheduleSet.collectAsState()
    if (mainScheduleSet) {
        ScheduleScreen(
            backButtonVisible = false,
            onBackClick = { },
            onScheduleClick = onScheduleClick,
            onError = onError,
            viewModel = viewModel,
            setUiState = setUiState,
            modifier = modifier
        )
    }
    else {
        MainScheduleNotSetScreen(setUiState = setUiState, modifier = modifier)
    }
}

@Composable
private fun MainScheduleNotSetScreen(
    setUiState: SetUiStateLambda,
    modifier: Modifier
) {
    LaunchedEffect(true) {
        setUiState({}, true)
    }
    InnerScaffold(
        modifier = modifier,
        topBar = {
            BasicTopAppBar(
                title = stringResource(R.string.title_home),
                onSettingsClick = {

                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResourceWithInlineContent(R.string.message_main_schedule_not_set),
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
}