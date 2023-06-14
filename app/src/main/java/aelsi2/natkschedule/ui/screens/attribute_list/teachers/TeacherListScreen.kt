package aelsi2.natkschedule.ui.screens.attribute_list.teachers

import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.screens.attribute_list.AttributeListScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeacherListScreen(
    setUiState: SetUiStateLambda,
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onError: suspend () -> Unit = {},
    viewModel: TeacherListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_teacher_list),
        searchPlaceholderText = stringResource(R.string.search_teachers_placeholder),
        filters = {},
        onAttributeClick = onAttributeClick,
        onSettingsClick = onSettingsClick,
        onError = onError,
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}