package aelsi2.natkschedule.ui

import aelsi2.compose.material3.pullrefresh.PullRefreshIndicator
import aelsi2.compose.material3.pullrefresh.PullRefreshState
import aelsi2.compose.material3.pullrefresh.pullRefresh
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.components.*
import aelsi2.natkschedule.ui.screens.attribute_list.ClassroomListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.FavoritesListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.GroupListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.TeacherListScreen
import aelsi2.natkschedule.ui.screens.attribute_list.attributeListTab
import aelsi2.natkschedule.ui.screens.attribute_list.favoritesListTab
import aelsi2.natkschedule.ui.screens.schedule.MainScheduleScreen
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.animation.with

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
        modifier = Modifier.run {
            val state = appState.pullRefreshState
            if (state == null) this else pullRefresh(state)
        }.run {
            val connection = appState.topAppBarNestedScrollConnection
            if (connection == null) this else nestedScroll(connection)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = appState.navController,
                startDestination = TopLevelRoutes.HOME_ROUTE
            ) {
                composable(TopLevelRoutes.HOME_ROUTE) {
                    MainScheduleScreen(appState::setUiState)
                }
                favoritesListTab(
                    route = TopLevelRoutes.FAVORITES_ROUTE,
                    onScheduleBackClick = appState::navigateBack,
                    onNavigateToSchedule = appState::navigateToFavoriteSchedule,
                    setUiState = appState::setUiState
                )
                attributeListTab(
                    route = TopLevelRoutes.TEACHERS_ROUTE,
                    scheduleType = ScheduleType.Teacher,
                    onScheduleBackClick = appState::navigateBack,
                    setUiState = appState::setUiState
                ) {setUiState ->
                    TeacherListScreen(
                        onAttributeClick = {
                            appState.navigateToSchedule(it)
                        },
                        setUiState = setUiState
                    )
                }
                attributeListTab(
                    route = TopLevelRoutes.CLASSROOMS_ROUTE,
                    scheduleType = ScheduleType.Classroom,
                    setUiState = appState::setUiState,
                    onScheduleBackClick = appState::navigateBack
                ) {setUiState ->
                    ClassroomListScreen(
                        onAttributeClick = {
                            appState.navigateToSchedule(it)
                        },
                        setUiState = setUiState
                    )
                }
                attributeListTab(
                    route = TopLevelRoutes.GROUPS_ROUTE,
                    scheduleType = ScheduleType.Group,
                    setUiState = appState::setUiState,
                    onScheduleBackClick = appState::navigateBack
                ) {setUiState ->
                    GroupListScreen(
                        onAttributeClick = {
                            appState.navigateToSchedule(it)
                        },
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

