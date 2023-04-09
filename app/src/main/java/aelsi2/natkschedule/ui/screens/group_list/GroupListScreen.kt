package aelsi2.natkschedule.ui.screens.group_list

import aelsi2.natkschedule.domain.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.components.AttributeList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupListScreen(
    onGroupClick: (ScheduleIdentifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<GroupListScreenViewModel>()
    val attributes = viewModel.attributes.collectAsState().value
    val state = viewModel.state.collectAsState().value
    AttributeList(
        attributes = attributes,
        isRefreshing = state == ScreenState.Loading,
        onRefresh = {
            viewModel.update()
        },
        onAttributeClick = onGroupClick,
        modifier = modifier
    )
}