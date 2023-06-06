package aelsi2.natkschedule.ui

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
import androidx.navigation.NavBackStackEntry
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
        .currentBackStackEntryAsState().value.destinationStartsWithRoute(route)

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
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToSchedule(scheduleIdentifier: ScheduleIdentifier) {
        val topRoute = when (scheduleIdentifier.type) {
            ScheduleType.Teacher -> TopLevelRoutes.TEACHERS_ROUTE
            ScheduleType.Classroom -> TopLevelRoutes.CLASSROOMS_ROUTE
            ScheduleType.Group -> TopLevelRoutes.GROUPS_ROUTE
        }
        if (!navController.currentBackStackEntry.destinationStartsWithRoute(topRoute)) {
            //Возвращаемся в корень и переходим на вкладку, восстанавливая ее back stack
            navController.navigate(route = topRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            //Сносим восстановленный back stack
            navController.popBackStack(route = topRoute, inclusive = true, saveState = false)
            //Снова переходим на вкладку
            navController.navigate(topRoute) {
                launchSingleTop = true
            }
        }
        navController.navigateToSchedule(
            route = topRoute,
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

    private fun NavBackStackEntry?.destinationStartsWithRoute(route: String): Boolean =
        this?.destination?.route?.startsWith(route) == true
}