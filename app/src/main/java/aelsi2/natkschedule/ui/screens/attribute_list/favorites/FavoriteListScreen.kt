package aelsi2.natkschedule.ui.screens.attribute_list.favorites

import aelsi2.compose.material3.rememberInlineIcons
import aelsi2.compose.material3.stringResourceWithInlineContent
import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.BasicTopAppBar
import aelsi2.natkschedule.ui.components.FilterChipRow
import aelsi2.natkschedule.ui.components.InnerScaffold
import aelsi2.natkschedule.ui.components.SelectableFilterChip
import aelsi2.natkschedule.ui.screens.attribute_list.AttributeListScreen
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
fun FavoritesListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    onError: suspend () -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: FavoriteListScreenViewModel = koinViewModel()
) {
    val notEmpty by viewModel.isNotEmpty.collectAsState()
    if (notEmpty) {
        AttributeListScreen(
            title = stringResource(R.string.favorites_tab_name),
            searchPlaceholderText = stringResource(R.string.search_favorites_placeholder),
            filters = {
                FilterChipRow {
                    val selectedType by viewModel.selectedScheduleType.collectAsState()
                    SelectableFilterChip(
                        text = stringResource(R.string.filter_favorite_list_all),
                        selected = selectedType == null,
                        onClick = {
                            viewModel.selectScheduleType(null)
                        }
                    )
                    SelectableFilterChip(
                        text = stringResource(R.string.filter_favorite_list_groups),
                        selected = selectedType == ScheduleType.Group,
                        onClick = {
                            viewModel.selectScheduleType(ScheduleType.Group)
                        }
                    )
                    SelectableFilterChip(
                        text = stringResource(R.string.filter_favorite_list_teachers),
                        selected = selectedType == ScheduleType.Teacher,
                        onClick = {
                            viewModel.selectScheduleType(ScheduleType.Teacher)
                        }
                    )
                    SelectableFilterChip(
                        text = stringResource(R.string.filter_favorite_list_classrooms),
                        selected = selectedType == ScheduleType.Classroom,
                        onClick = {
                            viewModel.selectScheduleType(ScheduleType.Classroom)
                        }
                    )
                }
            },
            onAttributeClick = onAttributeClick,
            onError = onError,
            setUiState = setUiState,
            modifier = modifier,
            viewModel = viewModel
        )
    }
    else {
        FavoritesEmptyScreen(setUiState = setUiState, modifier = modifier)
    }
}

@Composable
private fun FavoritesEmptyScreen(
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
                title = stringResource(R.string.title_favorite_list),
                onSettingsClick = {

                }
            )
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResourceWithInlineContent(R.string.message_no_favorites),
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