package aelsi2.natkschedule.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
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
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    navController: NavHostController = rememberNavController()
) : ScheduleAppState =
    remember(navController, snackBarHostState) {
        ScheduleAppState(snackBarHostState, navController)
    }

class ScheduleAppState(
    val snackBarHostState: SnackbarHostState,
    val navController: NavHostController
) {
    @Composable
    fun isAtTopRoute(
        route: String
    ): Boolean = navController
        .currentBackStackEntryAsState().value?.destination?.route?.startsWith(route) ?: false

    fun navigateToTab(route: String) {
        val alreadyAtTab = navController.currentDestination?.route?.startsWith(route) ?: false
        if (!alreadyAtTab) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                    inclusive = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
        else {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    fun navigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}

data class TopBarState(
    val titleText: String = "",
    val visible: Boolean = true,
    val actions: (@Composable RowScope.() -> Unit)? = null,
    val bottomContent: (@Composable () -> Unit)? = null
)

data class BottomBarState(
    val visible: Boolean
)