package aelsi2.natkschedule.ui.screens.attribute_list

 import aelsi2.compose.RecomposeLaunchedEffect
import aelsi2.compose.material3.TopAppBarDefaults
import aelsi2.compose.material3.pullrefresh.rememberPullRefreshState
import aelsi2.natkschedule.domain.model.ScreenState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.ui.SetUiStateLambda
 import aelsi2.natkschedule.ui.components.InnerScaffold
 import aelsi2.natkschedule.ui.components.attribute_list.AttributeList
import aelsi2.natkschedule.ui.components.attribute_list.AttributeListTopAppBar
import aelsi2.natkschedule.ui.components.attribute_list.AttributeSearchBar
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributeListScreen(
    title: String,
    searchPlaceholderText: String,
    filters: @Composable () -> Unit,
    onAttributeClick: (ScheduleIdentifier) -> Unit,
    onSettingsClick: () -> Unit,
    onError: suspend () -> Unit,
    setUiState: SetUiStateLambda,
    modifier: Modifier,
    viewModel: AttributeListScreenViewModel
) {
    var searchActive by rememberSaveable { mutableStateOf(false) }
    var searchRequestFocus by remember { mutableStateOf(false) }

    val screenState by viewModel.state.collectAsState()
    val hasFiltersSet by viewModel.hasFiltersSet.collectAsState()

    BackHandler(enabled = hasFiltersSet || searchActive) {
        searchActive = false
        viewModel.resetFilters()
    }
    LaunchedEffect(screenState){
        when (screenState) {
            ScreenState.Error -> onError()
            else -> Unit
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = screenState == ScreenState.Loading,
        onRefresh = viewModel::refresh,
        refreshThreshold = 64.dp,
        refreshingOffset = 64.dp
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(true) {
        setUiState({}, true)
    }

    InnerScaffold(
        modifier = modifier,
        nestedScrollConnection = scrollBehavior.nestedScrollConnection,
        pullRefreshState = pullRefreshState,
        topBar = {
            if (searchActive) {
                val focusRequester = remember {
                    FocusRequester()
                }
                val searchText by viewModel.searchString.collectAsState()
                AttributeSearchBar(
                    searchText = searchText,
                    onTextChange = viewModel::setSearchString,
                    onBackClick = {
                        searchActive = false
                        viewModel.resetFilters()
                    },
                    onClearClick = {
                        searchRequestFocus = true
                        viewModel.resetSearchString()
                    },
                    placeholderText = searchPlaceholderText,
                    modifier = Modifier.focusRequester(focusRequester)
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
                    onSettingsClick = onSettingsClick,
                    onRefreshClick = viewModel::refresh,
                    scrollBehavior = scrollBehavior
                )
            }
        }
    ) {
        val attributes by viewModel.attributes.collectAsState()
        RecomposeLaunchedEffect(attributes) {
            lazyListState.scrollToItem(0)
            scrollBehavior.state.heightOffset = 0f
            scrollBehavior.state.contentOffset = 0f
        }
        AttributeList(
            lazyListState = lazyListState,
            attributes = attributes,
            onAttributeClick = onAttributeClick,
            modifier = Modifier.fillMaxSize(),
            filters = filters
        )
    }
}