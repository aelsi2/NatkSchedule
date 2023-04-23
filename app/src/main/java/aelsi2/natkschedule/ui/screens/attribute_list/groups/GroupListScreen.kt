package aelsi2.natkschedule.ui.screens.attribute_list.groups

import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.DropdownFilterChip
import aelsi2.natkschedule.ui.components.FilterChipRow
import aelsi2.natkschedule.ui.screens.attribute_list.AttributeListScreen
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    onError: suspend () -> Unit = {},
    viewModel: GroupListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_group_list),
        searchPlaceholderText = stringResource(R.string.search_groups_placeholder),
        filters = {
            FilterChipRow {
                val programs by viewModel.programs.collectAsState()
                val selectedProgram by viewModel.selectedProgram.collectAsState()
                var programsExpanded by remember { mutableStateOf(false) }
                DropdownFilterChip(
                    text = selectedProgram ?: stringResource(R.string.filter_group_list_program),
                    hasSelectedValue = selectedProgram != null,
                    isExpanded = programsExpanded,
                    onChangeExpandedRequest = { expanded ->
                        if (!expanded || programs.isNotEmpty()) {
                            programsExpanded = expanded
                        }
                    },
                    onClearClick = viewModel::resetSelectedProgram
                ) {
                    for (program in programs) {
                        DropdownMenuItem(
                            text = {
                                Text(program)
                            },
                            onClick = {
                                viewModel.selectProgram(program)
                                programsExpanded = false
                            }
                        )
                    }
                }
                val selectedYear by viewModel.selectedYear.collectAsState()
                var yearsExpanded by remember { mutableStateOf(false) }
                DropdownFilterChip(
                    text = run {
                        val year = selectedYear
                        if (year == null) {
                            stringResource(R.string.filter_group_list_year)
                        } else {
                            stringResource(R.string.filter_group_list_year_value, year)
                        }
                    },
                    hasSelectedValue = selectedYear != null,
                    isExpanded = yearsExpanded,
                    onChangeExpandedRequest = { expanded ->
                        yearsExpanded = expanded
                    },
                    onClearClick = viewModel::resetSelectedYear
                ) {
                    for (year in viewModel.years) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.filter_group_list_year_value, year))
                            },
                            onClick = {
                                viewModel.selectYear(year)
                                yearsExpanded = false
                            }
                        )
                    }
                }
            }
        },
        onAttributeClick = onAttributeClick,
        onError = onError,
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}