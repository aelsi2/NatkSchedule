package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.natkschedule.domain.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.components.LectureList
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    scheduleIdentifier: ScheduleIdentifier? = null
) {
    val viewModel = if (scheduleIdentifier == null) {
        koinViewModel<ScheduleScreenViewModel>(
            qualifier = named("main")
        )
    }
    else {
        koinViewModel<ScheduleScreenViewModel>(
            qualifier = named("other"),
            parameters = { parametersOf(scheduleIdentifier) }
        )
    }
    val days = viewModel.days.collectAsState().value
    val state = viewModel.state.collectAsState().value
    val identifier = viewModel.scheduleIdentifier.collectAsState().value
    LectureList(
        days = days,
        viewModel::getLectureState,
        enablePullToRefresh = true,
        isRefreshing = state == ScreenState.Loading,
        onRefresh = {
            viewModel.update()
        },
        displayTeacher = identifier?.type != ScheduleType.TEACHER,
        displayClassroom = identifier?.type != ScheduleType.CLASSROOM,
        displayGroup = identifier?.type != ScheduleType.GROUP,
        displaySubgroup = identifier?.type == ScheduleType.GROUP,
        modifier = modifier
    )
}

@Preview
@Composable
fun ScheduleScreenPreview() {
    ScheduleScreen(
        modifier = Modifier.size(360.dp, 640.dp)
    )
}