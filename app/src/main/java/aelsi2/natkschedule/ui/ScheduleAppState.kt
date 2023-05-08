package aelsi2.natkschedule.ui

import aelsi2.compose.material3.pullrefresh.PullRefreshState
import aelsi2.natkschedule.data.network.ConnectivityManagerNetworkMonitor
import aelsi2.natkschedule.data.network.NetworkMonitor
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import aelsi2.natkschedule.ui.screens.attribute_list.navigateToFavoriteSchedule
import aelsi2.natkschedule.ui.screens.attribute_list.navigateToSchedule
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.get

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
    navController: NavHostController = rememberNavController(),
    networkMonitor: NetworkMonitor = get()
): ScheduleAppState =
    remember(snackBarHostState, navController, networkMonitor) {
        ScheduleAppState(snackBarHostState, navController, networkMonitor)
    }

@Stable
class ScheduleAppState(
    val snackBarHostState: SnackbarHostState,
    val navController: NavHostController,
    val networkMonitor: NetworkMonitor
) {
    var onCurrentTabClick: () -> Unit by mutableStateOf({})
    var navigationBarVisible: Boolean by mutableStateOf(true)

    @Composable
    fun isAtTopRoute(
        route: String
    ): Boolean = navController
        .currentBackStackEntryAsState().value?.destination?.route?.startsWith(route) ?: false

    suspend fun showPersistentMessage(text: String) {
        snackBarHostState.showSnackbar(
            text,
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite
        )
    }

    suspend fun showMessage(text: String) {
        snackBarHostState.showSnackbar(
            text
        )
    }

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
                ScheduleType.Teacher -> TopLevelRoutes.TEACHERS_ROUTE
                ScheduleType.Classroom -> TopLevelRoutes.CLASSROOMS_ROUTE
                ScheduleType.Group -> TopLevelRoutes.GROUPS_ROUTE
            },
            stringId = scheduleIdentifier.stringId
        )
    }

    fun navigateToFavoriteSchedule(scheduleIdentifier: ScheduleIdentifier) {
        navController.navigateToFavoriteSchedule(
            route = TopLevelRoutes.FAVORITES_ROUTE,
            scheduleIdentifier = scheduleIdentifier
        )
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}