package aelsi2.natkschedule.ui.screens.attribute_list.classroom_list

import aelsi2.natkschedule.domain.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.components.AttributeList
import aelsi2.natkschedule.ui.screens.attribute_list.group_list.GroupListScreenViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun ClassroomListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<ClassroomListScreenViewModel>()
    val attributes = viewModel.attributes.collectAsState().value
    val state = viewModel.state.collectAsState().value
    AttributeList(
        attributes = attributes,
        isRefreshing = state == ScreenState.Loading,
        onRefresh = {
            viewModel.update()
        },
        onAttributeClick = onAttributeClick,
        modifier = modifier
    )
}