package aelsi2.natkschedule.ui.screens.attribute_list

import aelsi2.compose.LaunchedEffectOnUpdate
import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.compose.material3.pullrefresh.rememberPullRefreshState
import aelsi2.natkschedule.R
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.SetUiStateLambda
import aelsi2.natkschedule.ui.components.attribute_list.AttributeList
import aelsi2.natkschedule.ui.components.attribute_list.AttributeListTopAppBar
import aelsi2.natkschedule.ui.components.attribute_list.AttributeSearchBar
import aelsi2.natkschedule.ui.components.DropdownFilterChip
import aelsi2.natkschedule.ui.components.FilterChipRow
import aelsi2.natkschedule.ui.components.SelectableFilterChip
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset

@Composable
fun FavoritesListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: FavoritesListScreenViewModel = koinViewModel()
) {
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
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
fun TeacherListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: TeacherListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_teacher_list),
        searchPlaceholderText = stringResource(R.string.search_teachers_placeholder),
        filters = {},
        onAttributeClick = onAttributeClick,
        setUiState = setUiState,
        modifier = modifier,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassroomListScreen(
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier = Modifier,
    viewModel: ClassroomListScreenViewModel = koinViewModel()
) {
    AttributeListScreen(
        title = stringResource(R.string.title_classroom_list),
        searchPlaceholderText = stringResource(R.string.search_classrooms_placeholder),
        filters = { activateSearch ->
            FilterChipRow {
                val selectedAddress by viewModel.selectedAddress.collectAsState()
                var addressesExpanded by remember { mutableStateOf(false) }
                DropdownFilterChip(
                    text = selectedAddress ?: stringResource(R.string.filter_classroom_list_address),
                    hasSelectedValue = selectedAddress != null,
                    isExpanded = addressesExpanded,
                    onChangeExpandedRequest = { expanded ->
                        addressesExpanded = expanded
                    },
                    onClearClick = viewModel::resetSelectedAddress
                ) {
                    val addresses by viewModel.addresses.collectAsState()
                    for (address in addresses) {
                        DropdownMenuItem(
                            text = {
                                Text(address)
                            },
                            onClick = {
                                activateSearch()
                                viewModel.selectAddress(address)
                                addressesExpanded = false
                            }
                        )
                    }
                }
            }
        },
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
        searchPlaceholderText = stringResource(R.string.search_groups_placeholder),
        filters = { activateSearch ->
            FilterChipRow {
                val selectedProgram by viewModel.selectedProgram.collectAsState()
                var programsExpanded by remember { mutableStateOf(false) }
                DropdownFilterChip(
                    text = selectedProgram ?: stringResource(R.string.filter_group_list_program),
                    hasSelectedValue = selectedProgram != null,
                    isExpanded = programsExpanded,
                    onChangeExpandedRequest = { expanded ->
                        programsExpanded = expanded
                    },
                    onClearClick = viewModel::resetSelectedProgram
                ) {
                    val programs by viewModel.programs.collectAsState()
                    for (program in programs) {
                        DropdownMenuItem(
                            text = {
                                Text(program)
                            },
                            onClick = {
                                activateSearch()
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
                                activateSearch()
                                viewModel.selectYear(year)
                                yearsExpanded = false
                            }
                        )
                    }
                }
            }
        },
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
    searchPlaceholderText: String,
    filters: @Composable (activateSearch: () -> Unit) -> Unit,
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier,
    viewModel: AttributeListScreenViewModel
) {
    val lazyListState = rememberLazyListState()

    var searchActive by rememberSaveable { mutableStateOf(false) }
    var searchRequestFocus by remember { mutableStateOf(false) }

    val screenState by viewModel.state.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = screenState == ScreenState.Loading,
        onRefresh = viewModel::refresh,
        refreshThreshold = 64.dp,
        refreshingOffset = 64.dp
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val topAppBar: @Composable () -> Unit = remember {
        {
            if (searchActive) {
                BackHandler {
                    searchActive = false
                    viewModel.resetSearchAndFilters()
                }
                val focusRequester = remember {
                    FocusRequester()
                }
                val searchText by viewModel.searchString.collectAsState()
                AttributeSearchBar(
                    searchText = searchText,
                    onTextChange = viewModel::setSearchString,
                    onBackClick = {
                        searchActive = false
                        viewModel.resetSearchAndFilters()
                    },
                    onClearClick = {
                        searchRequestFocus = true
                        viewModel.resetSearchString()
                    },
                    placeholderText = searchPlaceholderText,
                    modifier = modifier.focusRequester(focusRequester)
                )
                LaunchedEffect(searchRequestFocus) {
                    if (searchRequestFocus) {
                        focusRequester.requestFocus()
                        searchRequestFocus = false
                    }
                }
            } else {
                AttributeListTopAppBar(
                    title = title,
                    onSearchClick = {
                        searchActive = true
                        searchRequestFocus = true
                    },
                    onRefreshClick = viewModel::refresh,
                    scrollBehavior = scrollBehavior
                )
            }
        }
    }
    LaunchedEffect(true) {
        setUiState(topAppBar, scrollBehavior.nestedScrollConnection, pullRefreshState, true)
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {

        val attributes by viewModel.attributes.collectAsState()
        LaunchedEffectOnUpdate(attributes) {
            lazyListState.scrollToItem(0)
            scrollBehavior.state.heightOffset = 0f
            scrollBehavior.state.contentOffset = 0f
        }
        AttributeList(
            lazyListState = lazyListState,
            attributes = attributes,
            onAttributeClick = onAttributeClick,
            modifier = modifier.fillMaxSize(),
            filters = {
                filters { searchActive = true }
            }
        )
    }
}