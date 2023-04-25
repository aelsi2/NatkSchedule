package aelsi2.natkschedule.ui

import aelsi2.compose.RecomposeLaunchedEffect
import aelsi2.compose.material3.pullrefresh.PullRefreshIndicator
import aelsi2.compose.material3.pullrefresh.PullRefreshState
import aelsi2.compose.material3.pullrefresh.pullRefresh
import aelsi2.natkschedule.R
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.components.*
import aelsi2.natkschedule.ui.screens.attribute_list.classrooms.ClassroomListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.groups.GroupListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.teachers.TeacherListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.attributeListTab
import aelsi2.natkschedule.ui.screens.attribute_list.favoritesListTab
import aelsi2.natkschedule.ui.screens.schedule.main.MainScheduleScreen
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.animation.with
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

typealias SetUiStateLambda = (
    topAppBar: (@Composable () -> Unit)?,
    topAppBarNestedScroll: NestedScrollConnection?,
    pullRefreshState: PullRefreshState?,
    navigationBarVisible: Boolean
) -> Unit

fun ScheduleAppState.setUiState(
    topAppBar: (@Composable () -> Unit)? = null,
    topAppBarNestedScroll: NestedScrollConnection? = null,
    pullRefreshState: PullRefreshState? = null,
    navigationBarVisible: Boolean = true
) {
    topAppBarContent = topAppBar
    topAppBarNestedScrollConnection = topAppBarNestedScroll
    this.pullRefreshState = pullRefreshState
    this.navigationBarVisible = navigationBarVisible
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ScheduleApp(
    appState: ScheduleAppState = rememberScheduleAppState()
) {
    val noInternetMessage = stringResource(R.string.message_no_internet)
    val internetRestoredMessage = stringResource(R.string.message_internet_restored)

    var wasOnline by rememberSaveable { mutableStateOf(true) }
    val isOnline by appState.networkMonitor.isOnline.collectAsState(true)
    LaunchedEffect(isOnline) {
        if (isOnline){
            if (!wasOnline) {
                wasOnline = true
                appState.showMessage(internetRestoredMessage)
            }
        } else {
            wasOnline = false
            appState.showPersistentMessage(noInternetMessage)
        }
    }

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = appState.topAppBarContent,
                transitionSpec = {
                    (fadeIn()).with(fadeOut()).using(SizeTransform { _, _ -> snap(50) })
                }
            ) {
                it?.invoke()
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = appState.navigationBarVisible) {
                NavBar(
                    items = ScheduleAppTab.values().toList(),
                    isItemSelected = { route ->
                        appState.isAtTopRoute(route)
                    },
                    onItemClick = { route -> appState.navigateToTab(route) },
                )
            }
        },
        snackbarHost = {
            SnackbarHost(appState.snackBarHostState)
        },
        modifier = Modifier.run {
            val state = appState.pullRefreshState
            if (state == null) this else pullRefresh(state)
        }.run {
            val connection = appState.topAppBarNestedScrollConnection
            if (connection == null) this else nestedScroll(connection)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            val attributeListErrorMessage =
                stringResource(R.string.message_attribute_list_error)
            val scheduleErrorMessage =
                stringResource(R.string.message_schedule_error)

            suspend fun onListError() {
                if (appState.snackBarHostState.currentSnackbarData == null) {
                    appState.showMessage(attributeListErrorMessage)
                }
            }
            suspend fun onScheduleError() {
                if (appState.snackBarHostState.currentSnackbarData == null) {
                    appState.showMessage(scheduleErrorMessage)
                }
            }

            NavHost(
                navController = appState.navController,
                startDestination = TopLevelRoutes.HOME_ROUTE
            ) {
                composable(TopLevelRoutes.HOME_ROUTE) {
                    MainScheduleScreen(
                        appState::setUiState,
                        onError = ::onScheduleError,
                    )
                }
                favoritesListTab(
                    route = TopLevelRoutes.FAVORITES_ROUTE,
                    onScheduleBackClick = appState::navigateBack,
                    onNavigateToSchedule = appState::navigateToFavoriteSchedule,
                    onListError = ::onListError,
                    onScheduleError = ::onScheduleError,
                    setUiState = appState::setUiState
                )
                attributeListTab(
                    route = TopLevelRoutes.TEACHERS_ROUTE,
                    scheduleType = ScheduleType.Teacher,
                    onScheduleBackClick = appState::navigateBack,
                    onScheduleError = ::onScheduleError,
                    setUiState = appState::setUiState
                ) { setUiState ->
                    TeacherListScreen(
                        onAttributeClick = appState::navigateToSchedule,
                        onError = ::onListError,
                        setUiState = setUiState
                    )
                }
                attributeListTab(
                    route = TopLevelRoutes.CLASSROOMS_ROUTE,
                    scheduleType = ScheduleType.Classroom,
                    setUiState = appState::setUiState,
                    onScheduleError = ::onScheduleError,
                    onScheduleBackClick = appState::navigateBack
                ) { setUiState ->
                    ClassroomListScreen(
                        onAttributeClick = appState::navigateToSchedule,
                        onError = ::onListError,
                        setUiState = setUiState
                    )
                }
                attributeListTab(
                    route = TopLevelRoutes.GROUPS_ROUTE,
                    scheduleType = ScheduleType.Group,
                    setUiState = appState::setUiState,
                    onScheduleError = ::onScheduleError,
                    onScheduleBackClick = appState::navigateBack
                ) { setUiState ->
                    GroupListScreen(
                        onAttributeClick = appState::navigateToSchedule,
                        onError = ::onListError,
                        setUiState = setUiState
                    )
                }
            }
            val refreshState = appState.pullRefreshState
            if (refreshState != null) {
                PullRefreshIndicator(
                    state = refreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}