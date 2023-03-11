package aelsi2.natkschedule.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hierarchy
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
    val currentTopRoute : String?
        @Composable
        get() = navController
            .currentBackStackEntryAsState().value
            ?.destination?.hierarchy?.first()?.route

    fun navigateToTab(route : String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
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