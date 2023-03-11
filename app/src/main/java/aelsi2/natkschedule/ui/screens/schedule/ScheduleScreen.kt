package aelsi2.natkschedule.ui.screens.schedule

import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.components.LectureList
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
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<ScheduleScreenViewModel>(
        qualifier = named("other"),
        parameters = { parametersOf(ScheduleIdentifier(ScheduleType.GROUP, "ПР-20.101")) })
    val lectures = viewModel.lectures.collectAsState().value
    LectureList(lectures = lectures, enablePullToRefresh = false, refreshing = false, modifier = modifier)
}

@Preview
@Composable
fun ScheduleScreenPreview() {
    ScheduleScreen(
        modifier = Modifier.size(360.dp, 640.dp)
    )
}