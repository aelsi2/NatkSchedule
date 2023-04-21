package aelsi2.natkschedule.ui

import aelsi2.compose.material3.pullrefresh.PullRefreshState
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.screens.attribute_list.navigateToSchedule
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

object TopLevelRoutes {
    const val HOME_ROUTE = "home"
    const val FAVORITES_ROUTE = "favorites"
    const val GROUPS_ROUTE = "groups"
    const val TEACHERS_ROUTE = "teachers"
    const val CLASSROOMS_ROUTE = "classrooms"
}

@Composable
fun rememberScheduleAppState(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navController: NavHostController = rememberNavController()
) : ScheduleAppState =
    remember(snackBarHostState, navController) {
        ScheduleAppState(snackBarHostState, navController)
    }

@Stable
class ScheduleAppState(
    val snackBarHostState: SnackbarHostState,
    val navController: NavHostController
) {
    var topAppBarContent: (@Composable () -> Unit)? by mutableStateOf(null)
    var topAppBarNestedScrollConnection: NestedScrollConnection? by mutableStateOf(null)
    var pullRefreshState: PullRefreshState? by mutableStateOf(null)
    var navigationBarVisible: Boolean by mutableStateOf(true)

    @Composable
    fun isAtTopRoute(
        route: String
    ): Boolean = navController
        .currentBackStackEntryAsState().value?.destination?.route?.startsWith(route) ?: false

    fun navigateToTab(route: String) {
        val alreadyAtTab = navController.currentDestination?.route?.startsWith(route) ?: false
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    fun navigateToSchedule(scheduleIdentifier: ScheduleIdentifier) {
        navController.navigateToSchedule(
            route = when (scheduleIdentifier.type) {
                ScheduleType.TEACHER -> TopLevelRoutes.TEACHERS_ROUTE
                ScheduleType.CLASSROOM -> TopLevelRoutes.CLASSROOMS_ROUTE
                ScheduleType.GROUP -> TopLevelRoutes.GROUPS_ROUTE
            },
            stringId = scheduleIdentifier.stringId
        )
    }
    fun navigateBack() {
        navController.popBackStack()
    }
}